package com.smartorders.notification.dto;

import com.smartorders.notification.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient email must be valid")
    private String recipientEmail;

    public NotificationRequest() {}

    public Long getCustomerId() { return customerId; }
    public Long getOrderId() { return orderId; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public String getRecipientEmail() { return recipientEmail; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setType(NotificationType type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
}