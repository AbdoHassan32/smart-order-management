package com.smartorders.order.client;

import com.smartorders.order.client.fallback.PaymentClientFallbackFactory;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.PaymentRequest;
import com.smartorders.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        fallbackFactory = PaymentClientFallbackFactory.class
)
public interface PaymentClient {

    @PostMapping("/api/v1/payments")
    BaseResponse<PaymentResponse> processPayment(@RequestBody PaymentRequest request);
}