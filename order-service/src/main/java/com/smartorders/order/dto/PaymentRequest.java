package com.smartorders.order.dto;

import java.math.BigDecimal;

public class PaymentRequest {

    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private String paymentMethod;

    public PaymentRequest() {}

    public PaymentRequest(Long orderId, Long customerId,
                          BigDecimal amount, String paymentMethod) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getOrderId() { return orderId; }
    public Long getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}