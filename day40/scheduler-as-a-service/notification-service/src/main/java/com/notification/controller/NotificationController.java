package com.notification.controller;

import com.notification.service.NotificationSchedulerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationSchedulerClient schedulerClient;
    
    @PostMapping("/send-batch")
    public Map<String, Object> scheduleBatchNotifications() {
        return schedulerClient.submitTask(
            "batch-email-notifications",
            "{\"type\": \"weekly-digest\", \"users\": 50000}",
            "http://localhost:8082/api/notifications/callback"
        );
    }
    
    @PostMapping("/callback")
    public void handleCallback(@RequestBody Map<String, Object> result) {
        System.out.println("Notification task completed: " + result);
    }
}
