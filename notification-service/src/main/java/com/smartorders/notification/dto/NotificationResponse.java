package com.smartorders.notification.dto;

import com.smartorders.notification.enums.NotificationStatus;
import com.smartorders.notification.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long customerId;
    private Long orderId;
    private NotificationType type;
    private NotificationStatus status;
    private String message;
    private String recipientEmail;
    private LocalDateTime sentAt;

    public NotificationResponse() {}

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Long getOrderId() { return orderId; }
    public NotificationType getType() { return type; }
    public NotificationStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public String getRecipientEmail() { return recipientEmail; }
    public LocalDateTime getSentAt() { return sentAt; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setType(NotificationType type) { this.type = type; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}