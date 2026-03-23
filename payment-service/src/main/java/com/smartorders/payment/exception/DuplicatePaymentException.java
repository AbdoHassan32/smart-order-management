package com.smartorders.payment.exception;

public class DuplicatePaymentException extends RuntimeException {

    public DuplicatePaymentException(Long orderId) {
        super("Payment already processed for order id: " + orderId);
    }
}