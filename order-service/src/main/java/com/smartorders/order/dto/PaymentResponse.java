package com.smartorders.order.dto;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String status;
    private String transactionReference;
    private String failureReason;

    public PaymentResponse() {}

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getTransactionReference() { return transactionReference; }
    public String getFailureReason() { return failureReason; }

    public void setId(Long id) { this.id = id; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setStatus(String status) { this.status = status; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}