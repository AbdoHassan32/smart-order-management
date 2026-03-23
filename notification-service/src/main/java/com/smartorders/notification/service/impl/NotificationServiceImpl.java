package com.smartorders.notification.service.impl;

import com.smartorders.notification.dto.NotificationRequest;
import com.smartorders.notification.dto.NotificationResponse;
import com.smartorders.notification.entity.Notification;
import com.smartorders.notification.enums.NotificationStatus;
import com.smartorders.notification.exception.NotificationNotFoundException;
import com.smartorders.notification.mapper.NotificationMapper;
import com.smartorders.notification.repository.NotificationRepository;
import com.smartorders.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Sending notification type: {} for order id: {} to: {}",
                request.getType(), request.getOrderId(), request.getRecipientEmail());

        Notification notification = new Notification();
        notification.setCustomerId(request.getCustomerId());
        notification.setOrderId(request.getOrderId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setRecipientEmail(request.getRecipientEmail());

        // Simulate sending — in production plug in SendGrid / SES / Twilio here
        boolean sent = simulateSend(request);

        if (sent) {
            notification.setStatus(NotificationStatus.SENT);
            log.info("Notification SENT successfully for order id: {}", request.getOrderId());
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            log.warn("Notification FAILED for order id: {}", request.getOrderId());
        }

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.info("Fetching notification with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByCustomerId(Long customerId) {
        log.info("Fetching notifications for customer id: {}", customerId);
        return notificationRepository.findAllByCustomerId(customerId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByOrderId(Long orderId) {
        log.info("Fetching notifications for order id: {}", orderId);
        return notificationRepository.findAllByOrderId(orderId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    // Simulates delivery — always succeeds for now
    // Replace with real provider call in production
    private boolean simulateSend(NotificationRequest request) {
        return true;
    }
}