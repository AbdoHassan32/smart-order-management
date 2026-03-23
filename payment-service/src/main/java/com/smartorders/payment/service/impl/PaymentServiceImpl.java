package com.smartorders.payment.service.impl;

import com.smartorders.payment.dto.PaymentRequest;
import com.smartorders.payment.dto.PaymentResponse;
import com.smartorders.payment.entity.Payment;
import com.smartorders.payment.enums.PaymentStatus;
import com.smartorders.payment.exception.DuplicatePaymentException;
import com.smartorders.payment.exception.PaymentNotFoundException;
import com.smartorders.payment.mapper.PaymentMapper;
import com.smartorders.payment.repository.PaymentRepository;
import com.smartorders.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order id: {}", request.getOrderId());

        // Business rule — prevent duplicate payment for same order
        if (paymentRepository.existsByOrderIdAndStatus(
                request.getOrderId(), PaymentStatus.SUCCESS)) {
            throw new DuplicatePaymentException(request.getOrderId());
        }

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setCustomerId(request.getCustomerId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        // Simulate payment processing
        // In a real system this would call a payment gateway (Stripe, PayPal, etc.)
        boolean paymentSucceeded = simulatePayment(request);

        if (paymentSucceeded) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().toUpperCase());
            log.info("Payment succeeded for order id: {}, ref: {}",
                    request.getOrderId(), payment.getTransactionReference());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Insufficient funds");
            log.warn("Payment failed for order id: {}", request.getOrderId());
        }

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment for order id: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for order id: " + orderId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        log.info("Fetching payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return paymentMapper.toResponse(payment);
    }

    /**
     * Simulates payment gateway response.
     * Rule: amounts ending in .99 always fail — useful for testing failure flow.
     * All other amounts succeed.
     */
    private boolean simulatePayment(PaymentRequest request) {
        String amountStr = request.getAmount().toPlainString();
        return !amountStr.endsWith(".99");
    }
}