package com.smartorders.inventory.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, int requested, int available) {
        super("Insufficient stock for product id: " + productId +
                ". Requested: " + requested + ", Available: " + available);
    }
}