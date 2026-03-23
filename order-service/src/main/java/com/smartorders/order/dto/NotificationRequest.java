package com.smartorders.order.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequest {

    private Long customerId;
    private Long orderId;
    private String type;
    private String message;
    private String recipientEmail;

    public NotificationRequest() {}

    public NotificationRequest(Long customerId, Long orderId,
                               String type, String message,
                               String recipientEmail) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.recipientEmail = recipientEmail;
    }

}