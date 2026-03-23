package com.smartorders.inventory.dto;

public class StockOperationResponse {

    private Long productId;
    private String productName;
    private String sku;
    private Integer requestedQuantity;
    private Integer availableQuantity;
    private String operation;
    private boolean success;

    public StockOperationResponse() {}

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public Integer getRequestedQuantity() { return requestedQuantity; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public String getOperation() { return operation; }
    public boolean isSuccess() { return success; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setSku(String sku) { this.sku = sku; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
    public void setOperation(String operation) { this.operation = operation; }
    public void setSuccess(boolean success) { this.success = success; }
}