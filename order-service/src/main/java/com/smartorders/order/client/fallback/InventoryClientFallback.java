package com.smartorders.order.client.fallback;

import com.smartorders.order.client.InventoryClient;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.ProductResponse;
import com.smartorders.order.dto.StockReservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryClientFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallback.class);
    private final Throwable cause;

    public InventoryClientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public BaseResponse<ProductResponse> getProductById(Long productId) {
        log.error("Inventory Service unavailable. Fallback triggered. Cause: {}",
                cause.getMessage());
        return null;
    }

    @Override
    public BaseResponse<Object> reserveStock(StockReservationRequest request) {
        log.error("Inventory Service unavailable for reserveStock. Cause: {}",
                cause.getMessage());
        return null;
    }

    @Override
    public BaseResponse<Object> releaseStock(StockReservationRequest request) {
        log.error("Inventory Service unavailable for releaseStock. Cause: {}",
                cause.getMessage());
        return null;
    }

    @Override
    public BaseResponse<Object> consumeStock(StockReservationRequest request) {
        log.error("Inventory Service unavailable for consumeStock. Cause: {}",
                cause.getMessage());
        return null;
    }
}