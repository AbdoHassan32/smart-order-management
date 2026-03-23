package com.smartorders.order.service.impl;

import com.smartorders.order.client.CustomerClient;
import com.smartorders.order.client.InventoryClient;
import com.smartorders.order.client.NotificationClient;
import com.smartorders.order.client.PaymentClient;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.*;
import com.smartorders.order.entity.Order;
import com.smartorders.order.entity.OrderItem;
import com.smartorders.order.enums.OrderStatus;
import com.smartorders.order.exception.OrderNotFoundException;
import com.smartorders.order.exception.OrderProcessingException;
import com.smartorders.order.mapper.OrderMapper;
import com.smartorders.order.repository.OrderRepository;
import com.smartorders.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerClient customerClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final NotificationClient notificationClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            CustomerClient customerClient,
                            InventoryClient inventoryClient,
                            PaymentClient paymentClient,
                            NotificationClient notificationClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerClient = customerClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.notificationClient = notificationClient;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer id: {}", request.getCustomerId());

        // Step 1 — Validate customer
        BaseResponse<CustomerResponse> customerResponse =
                callValidateCustomer(request.getCustomerId());

        if (customerResponse == null || !customerResponse.isSuccess()) {
            throw new OrderProcessingException(
                    "Customer validation failed for id: " + request.getCustomerId());
        }

        CustomerResponse customer = customerResponse.getData();

        // Step 2 — Fetch products and validate availability
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            BaseResponse<ProductResponse> productResponse =
                    callGetProduct(itemRequest.getProductId());

            if (productResponse == null || productResponse.getData() == null) {
                throw new OrderProcessingException(
                        "Product not found with id: " + itemRequest.getProductId());
            }

            ProductResponse product = productResponse.getData();

            if (!"ACTIVE".equals(product.getStatus())) {
                throw new OrderProcessingException(
                        "Product is not available: " + product.getName());
            }

            if (product.getAvailableQuantity() < itemRequest.getQuantity()) {
                throw new OrderProcessingException(
                        "Insufficient stock for product: " + product.getName() +
                                ". Requested: " + itemRequest.getQuantity() +
                                ", Available: " + product.getAvailableQuantity());
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        // Step 3 — Persist order as PENDING
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setNotes(request.getNotes());

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {} status: PENDING", savedOrder.getId());

        // Step 4 — Reserve stock
        for (OrderItem item : savedOrder.getItems()) {
            StockReservationRequest reservationRequest =
                    new StockReservationRequest(item.getProductId(), item.getQuantity());
            inventoryClient.reserveStock(reservationRequest);
        }
        log.info("Stock reserved for order id: {}", savedOrder.getId());

        // Step 5 — Process payment
        log.info("Processing payment for order id: {}", savedOrder.getId());
        PaymentRequest paymentRequest = new PaymentRequest(
                savedOrder.getId(),
                request.getCustomerId(),
                totalAmount,
                request.getPaymentMethod()
        );

        BaseResponse<PaymentResponse> paymentResponse =
                callProcessPayment(paymentRequest);

        if (paymentResponse == null || paymentResponse.getData() == null) {
            releaseAllStock(savedOrder);
            savedOrder.setStatus(OrderStatus.FAILED);
            orderRepository.save(savedOrder);
            throw new OrderProcessingException(
                    "Payment service unavailable for order id: " + savedOrder.getId());
        }

        PaymentResponse payment = paymentResponse.getData();

        // Step 6 — React to payment result
        if ("SUCCESS".equals(payment.getStatus())) {
            savedOrder.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(savedOrder);
            log.info("Order id: {} CONFIRMED. Transaction: {}",
                    savedOrder.getId(), payment.getTransactionReference());

            // Consume reserved stock
            for (OrderItem item : savedOrder.getItems()) {
                StockReservationRequest consumeRequest =
                        new StockReservationRequest(item.getProductId(), item.getQuantity());
                inventoryClient.consumeStock(consumeRequest);
            }
            log.info("Stock consumed for order id: {}", savedOrder.getId());

            sendNotification(
                    customer,
                    savedOrder.getId(),
                    "ORDER_CONFIRMED",
                    "Your order #" + savedOrder.getId() +
                            " has been confirmed. Total: $" + totalAmount
            );

        } else {
            savedOrder.setStatus(OrderStatus.FAILED);
            orderRepository.save(savedOrder);
            log.warn("Order id: {} FAILED. Reason: {}",
                    savedOrder.getId(), payment.getFailureReason());

            releaseAllStock(savedOrder);
            log.info("Stock released for order id: {}", savedOrder.getId());

            sendNotification(
                    customer,
                    savedOrder.getId(),
                    "ORDER_FAILED",
                    "Your order #" + savedOrder.getId() +
                            " could not be processed. Reason: " + payment.getFailureReason()
            );
        }

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        log.info("Fetching orders for customer id: {}", customerId);
        return orderRepository.findAllByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    // ── Circuit Breaker wrapped calls ─────────────────────────────────────────

    @CircuitBreaker(name = "customerService", fallbackMethod = "customerServiceFallback")
    @Retry(name = "customerService")
    public BaseResponse<CustomerResponse> callValidateCustomer(Long customerId) {
        return customerClient.validateCustomer(customerId);
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "inventoryGetProductFallback")
    @Retry(name = "inventoryService")
    public BaseResponse<ProductResponse> callGetProduct(Long productId) {
        return inventoryClient.getProductById(productId);
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentServiceFallback")
    public BaseResponse<PaymentResponse> callProcessPayment(PaymentRequest request) {
        return paymentClient.processPayment(request);
    }

    // ── Fallback methods ──────────────────────────────────────────────────────

    public BaseResponse<CustomerResponse> customerServiceFallback(
            Long customerId, Throwable t) {
        log.error("Circuit breaker open for customer-service. customerId: {}, cause: {}",
                customerId, t.getMessage());
        return null;
    }

    public BaseResponse<ProductResponse> inventoryGetProductFallback(
            Long productId, Throwable t) {
        log.error("Circuit breaker open for inventory-service. productId: {}, cause: {}",
                productId, t.getMessage());
        return null;
    }

    public BaseResponse<PaymentResponse> paymentServiceFallback(
            PaymentRequest request, Throwable t) {
        log.error("Circuit breaker open for payment-service. orderId: {}, cause: {}",
                request.getOrderId(), t.getMessage());
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void releaseAllStock(Order order) {
        for (OrderItem item : order.getItems()) {
            try {
                StockReservationRequest releaseRequest =
                        new StockReservationRequest(item.getProductId(), item.getQuantity());
                inventoryClient.releaseStock(releaseRequest);
            } catch (Exception e) {
                log.error("Failed to release stock for product id: {} order id: {}",
                        item.getProductId(), order.getId(), e);
            }
        }
    }

    private void sendNotification(CustomerResponse customer,
                                  Long orderId,
                                  String type,
                                  String message) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest(
                    customer.getId(),
                    orderId,
                    type,
                    message,
                    customer.getEmail()
            );
            notificationClient.sendNotification(notificationRequest);
            log.info("Notification sent for order id: {}, type: {}", orderId, type);
        } catch (Exception e) {
            log.error("Failed to send notification for order id: {}", orderId, e);
        }
    }
}