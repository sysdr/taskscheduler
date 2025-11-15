package com.taskscheduler.controller;

import com.taskscheduler.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EventController {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/file")
    public ResponseEntity<Map<String, String>> publishFileEvent(@RequestBody FileUploadEvent event) {
        log.info("Publishing file upload event: {}", event.getFileName());
        kafkaTemplate.send("file-events", event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "Event published");
        response.put("eventId", event.getEventId());
        response.put("topic", "file-events");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, String>> publishUserEvent(@RequestBody UserActionEvent event) {
        log.info("Publishing user action event: {} for user {}", 
                event.getActionType(), event.getUsername());
        kafkaTemplate.send("user-events", event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "Event published");
        response.put("eventId", event.getEventId());
        response.put("topic", "user-events");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/system")
    public ResponseEntity<Map<String, String>> publishSystemEvent(@RequestBody SystemHealthEvent event) {
        log.info("Publishing system health event: {}", event.getMetricType());
        kafkaTemplate.send("system-events", event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "Event published");
        response.put("eventId", event.getEventId());
        response.put("topic", "system-events");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/demo/file-upload")
    public ResponseEntity<Map<String, String>> demoFileUpload() {
        FileUploadEvent event = new FileUploadEvent();
        event.setBucketName("user-uploads");
        event.setFileName("vacation-photo-" + System.currentTimeMillis() + ".jpg");
        event.setFileType("image/jpeg");
        event.setFileSize(2048576L);
        event.setUploadedBy("user@example.com");
        
        return publishFileEvent(event);
    }

    @PostMapping("/demo/user-registration")
    public ResponseEntity<Map<String, String>> demoUserRegistration() {
        UserActionEvent event = new UserActionEvent();
        event.setUserId(UUID.randomUUID().toString());
        event.setUsername("newuser" + System.currentTimeMillis());
        event.setActionType("REGISTRATION");
        event.setEmail("newuser@example.com");
        
        return publishUserEvent(event);
    }

    @PostMapping("/demo/system-alert")
    public ResponseEntity<Map<String, String>> demoSystemAlert() {
        SystemHealthEvent event = new SystemHealthEvent();
        event.setMetricType("CPU_HIGH");
        event.setCurrentValue(92.5);
        event.setThreshold(85.0);
        event.setSeverity("CRITICAL");
        
        return publishSystemEvent(event);
    }
}
