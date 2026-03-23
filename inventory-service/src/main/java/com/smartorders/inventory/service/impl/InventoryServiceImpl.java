package com.smartorders.inventory.service.impl;

import com.smartorders.inventory.dto.StockOperationResponse;
import com.smartorders.inventory.dto.StockReservationRequest;
import com.smartorders.inventory.entity.Product;
import com.smartorders.inventory.enums.ProductStatus;
import com.smartorders.inventory.exception.InsufficientStockException;
import com.smartorders.inventory.exception.InvalidStockOperationException;
import com.smartorders.inventory.exception.ProductNotFoundException;
import com.smartorders.inventory.repository.ProductRepository;
import com.smartorders.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final ProductRepository productRepository;

    public InventoryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StockOperationResponse checkAvailability(Long productId, Integer quantity) {
        log.info("Checking availability for product id: {}, quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        StockOperationResponse response = new StockOperationResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setSku(product.getSku());
        response.setRequestedQuantity(quantity);
        response.setAvailableQuantity(product.getAvailableQuantity());
        response.setOperation("CHECK");
        response.setSuccess(product.getAvailableQuantity() >= quantity);
        return response;
    }

    @Override
    @Transactional
    public StockOperationResponse reserveStock(StockReservationRequest request) {
        log.info("Reserving stock for product id: {}, quantity: {}",
                request.getProductId(), request.getQuantity());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new InvalidStockOperationException(
                    "Product is not active: " + request.getProductId());
        }

        int available = product.getAvailableQuantity();
        if (available < request.getQuantity()) {
            throw new InsufficientStockException(
                    request.getProductId(), request.getQuantity(), available);
        }

        product.setReservedQuantity(product.getReservedQuantity() + request.getQuantity());
        productRepository.save(product);

        log.info("Stock reserved successfully for product id: {}", request.getProductId());

        StockOperationResponse response = new StockOperationResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setSku(product.getSku());
        response.setRequestedQuantity(request.getQuantity());
        response.setAvailableQuantity(product.getAvailableQuantity());
        response.setOperation("RESERVE");
        response.setSuccess(true);
        return response;
    }

    @Override
    @Transactional
    public StockOperationResponse releaseStock(StockReservationRequest request) {
        log.info("Releasing stock for product id: {}, quantity: {}",
                request.getProductId(), request.getQuantity());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        int newReserved = product.getReservedQuantity() - request.getQuantity();
        if (newReserved < 0) {
            throw new InvalidStockOperationException(
                    "Cannot release more than reserved quantity for product: "
                            + request.getProductId());
        }

        product.setReservedQuantity(newReserved);
        productRepository.save(product);

        log.info("Stock released successfully for product id: {}", request.getProductId());

        StockOperationResponse response = new StockOperationResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setSku(product.getSku());
        response.setRequestedQuantity(request.getQuantity());
        response.setAvailableQuantity(product.getAvailableQuantity());
        response.setOperation("RELEASE");
        response.setSuccess(true);
        return response;
    }

    @Override
    @Transactional
    public StockOperationResponse consumeStock(StockReservationRequest request) {
        log.info("Consuming stock for product id: {}, quantity: {}",
                request.getProductId(), request.getQuantity());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        int newReserved = product.getReservedQuantity() - request.getQuantity();
        int newStock = product.getStockQuantity() - request.getQuantity();

        if (newReserved < 0 || newStock < 0) {
            throw new InvalidStockOperationException(
                    "Cannot consume more than reserved/available quantity for product: "
                            + request.getProductId());
        }

        product.setReservedQuantity(newReserved);
        product.setStockQuantity(newStock);

        if (newStock == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }

        productRepository.save(product);

        log.info("Stock consumed successfully for product id: {}", request.getProductId());

        StockOperationResponse response = new StockOperationResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setSku(product.getSku());
        response.setRequestedQuantity(request.getQuantity());
        response.setAvailableQuantity(product.getAvailableQuantity());
        response.setOperation("CONSUME");
        response.setSuccess(true);
        return response;
    }
}