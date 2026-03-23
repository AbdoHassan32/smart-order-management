package com.smartorders.order.client.fallback;

import com.smartorders.order.client.PaymentClient;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.PaymentRequest;
import com.smartorders.order.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentClientFallback implements PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientFallback.class);
    private final Throwable cause;

    public PaymentClientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public BaseResponse<PaymentResponse> processPayment(PaymentRequest request) {
        log.error("Payment Service unavailable. Fallback triggered. Cause: {}",
                cause.getMessage());
        return null;
    }
}