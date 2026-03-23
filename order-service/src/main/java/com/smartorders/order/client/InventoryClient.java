package com.smartorders.order.client;

import com.smartorders.order.client.fallback.InventoryClientFallbackFactory;
import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.ProductResponse;
import com.smartorders.order.dto.StockReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "inventory-service",
        fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryClient {

    @GetMapping("/api/v1/products/{id}")
    BaseResponse<ProductResponse> getProductById(@PathVariable("id") Long productId);

    @PostMapping("/api/v1/inventory/reserve")
    BaseResponse<Object> reserveStock(@RequestBody StockReservationRequest request);

    @PostMapping("/api/v1/inventory/release")
    BaseResponse<Object> releaseStock(@RequestBody StockReservationRequest request);

    @PostMapping("/api/v1/inventory/consume")
    BaseResponse<Object> consumeStock(@RequestBody StockReservationRequest request);
}