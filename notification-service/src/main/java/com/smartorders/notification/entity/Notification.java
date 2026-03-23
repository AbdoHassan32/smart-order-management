package com.smartorders.notification.entity;

import com.smartorders.notification.enums.NotificationStatus;
import com.smartorders.notification.enums.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false, length = 500)
    private String message;

    @Column
    private String recipientEmail;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    public Notification() {}

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