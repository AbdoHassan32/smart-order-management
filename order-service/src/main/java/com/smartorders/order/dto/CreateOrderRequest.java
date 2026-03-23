package com.smartorders.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String notes;

    public CreateOrderRequest() {}

    public Long getCustomerId() { return customerId; }
    public List<OrderItemRequest> getItems() { return items; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getNotes() { return notes; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setNotes(String notes) { this.notes = notes; }
}