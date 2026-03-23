package com.smartorders.notification.repository;

import com.smartorders.notification.entity.Notification;
import com.smartorders.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByCustomerId(Long customerId);

    List<Notification> findAllByOrderId(Long orderId);

    List<Notification> findAllByType(NotificationType type);
}