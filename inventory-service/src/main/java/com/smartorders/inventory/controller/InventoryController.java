package com.smartorders.inventory.controller;

import com.smartorders.inventory.common.BaseResponse;
import com.smartorders.inventory.dto.StockOperationResponse;
import com.smartorders.inventory.dto.StockReservationRequest;
import com.smartorders.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Stock management endpoints")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check stock availability")
    public ResponseEntity<BaseResponse<StockOperationResponse>> checkAvailability(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        StockOperationResponse response = inventoryService.checkAvailability(productId, quantity);
        return ResponseEntity.ok(BaseResponse.success("Availability checked", response));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock — called by Order Service")
    public ResponseEntity<BaseResponse<StockOperationResponse>> reserveStock(
            @Valid @RequestBody StockReservationRequest request) {
        StockOperationResponse response = inventoryService.reserveStock(request);
        return ResponseEntity.ok(BaseResponse.success("Stock reserved successfully", response));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock — called when payment fails")
    public ResponseEntity<BaseResponse<StockOperationResponse>> releaseStock(
            @Valid @RequestBody StockReservationRequest request) {
        StockOperationResponse response = inventoryService.releaseStock(request);
        return ResponseEntity.ok(BaseResponse.success("Stock released successfully", response));
    }

    @PostMapping("/consume")
    @Operation(summary = "Consume reserved stock — called when payment succeeds")
    public ResponseEntity<BaseResponse<StockOperationResponse>> consumeStock(
            @Valid @RequestBody StockReservationRequest request) {
        StockOperationResponse response = inventoryService.consumeStock(request);
        return ResponseEntity.ok(BaseResponse.success("Stock consumed successfully", response));
    }
}