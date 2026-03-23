package com.smartorders.notification.mapper;

import com.smartorders.notification.dto.NotificationResponse;
import com.smartorders.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setCustomerId(notification.getCustomerId());
        response.setOrderId(notification.getOrderId());
        response.setType(notification.getType());
        response.setStatus(notification.getStatus());
        response.setMessage(notification.getMessage());
        response.setRecipientEmail(notification.getRecipientEmail());
        response.setSentAt(notification.getSentAt());
        return response;
    }
}