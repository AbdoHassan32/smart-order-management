package com.smartorders.inventory.service;

import com.smartorders.inventory.dto.StockOperationResponse;
import com.smartorders.inventory.dto.StockReservationRequest;

public interface InventoryService {

    // Check availability without reserving
    StockOperationResponse checkAvailability(Long productId, Integer quantity);

    // Reserve stock — called before payment
    StockOperationResponse reserveStock(StockReservationRequest request);

    // Release reservation — called when payment fails
    StockOperationResponse releaseStock(StockReservationRequest request);

    // Consume reservation — called when payment succeeds
    StockOperationResponse consumeStock(StockReservationRequest request);
}