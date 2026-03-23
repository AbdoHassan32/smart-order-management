package com.smartorders.notification.controller;

import com.smartorders.notification.common.BaseResponse;
import com.smartorders.notification.dto.NotificationRequest;
import com.smartorders.notification.dto.NotificationResponse;
import com.smartorders.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Send a notification")
    public ResponseEntity<BaseResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Notification sent successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<BaseResponse<NotificationResponse>> getNotificationById(
            @PathVariable Long id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(BaseResponse.success("Notification retrieved successfully", response));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all notifications for a customer")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getNotificationsByCustomerId(
            @PathVariable Long customerId) {
        List<NotificationResponse> response =
                notificationService.getNotificationsByCustomerId(customerId);
        return ResponseEntity.ok(BaseResponse.success("Notifications retrieved successfully", response));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get all notifications for an order")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getNotificationsByOrderId(
            @PathVariable Long orderId) {
        List<NotificationResponse> response =
                notificationService.getNotificationsByOrderId(orderId);
        return ResponseEntity.ok(BaseResponse.success("Notifications retrieved successfully", response));
    }
}