package com.smartorders.notification.service;

import com.smartorders.notification.dto.NotificationRequest;
import com.smartorders.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    NotificationResponse getNotificationById(Long id);

    List<NotificationResponse> getNotificationsByCustomerId(Long customerId);

    List<NotificationResponse> getNotificationsByOrderId(Long orderId);
}