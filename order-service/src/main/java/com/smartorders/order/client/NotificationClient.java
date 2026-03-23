package com.smartorders.order.client;

import com.smartorders.order.common.BaseResponse;
import com.smartorders.order.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/v1/notifications")
    BaseResponse<Object> sendNotification(@RequestBody NotificationRequest request);
}