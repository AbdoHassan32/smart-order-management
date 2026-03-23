package com.smartorders.order.client;

import com.smartorders.order.client.fallback.CustomerClientFallbackFactory;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "customer-service",
        fallbackFactory = CustomerClientFallbackFactory.class
)
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{id}/validate")
    BaseResponse<CustomerResponse> validateCustomer(@PathVariable("id") Long customerId);
}