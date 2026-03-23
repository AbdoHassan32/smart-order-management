package com.smartorders.payment.service;

import com.smartorders.payment.dto.PaymentRequest;
import com.smartorders.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentByOrderId(Long orderId);

    PaymentResponse getPaymentById(Long id);
}