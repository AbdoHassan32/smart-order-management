package com.smartorders.order.client.fallback;

import com.smartorders.order.client.CustomerClient;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.CustomerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerClientFallback implements CustomerClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerClientFallback.class);
    private final Throwable cause;

    public CustomerClientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public BaseResponse<CustomerResponse> validateCustomer(Long customerId) {
        log.error("Customer Service unavailable. Fallback triggered. Cause: {}",
                cause.getMessage());
        return null;
    }
}