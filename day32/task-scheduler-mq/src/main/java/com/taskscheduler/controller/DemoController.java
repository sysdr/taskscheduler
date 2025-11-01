package com.taskscheduler.controller;

import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
public class DemoController {
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    @PostMapping("/create-sample-tasks")
    public ResponseEntity<Map<String, String>> createSampleTasks() {
        // Create email notification task
        Map<String, Object> emailPayload = new HashMap<>();
        emailPayload.put("recipient", "user@example.com");
        emailPayload.put("subject", "Welcome to Task Scheduler");
        emailPayload.put("body", "Your account has been activated");
        
        Task emailTask = new Task(UUID.randomUUID().toString(), "Welcome Email", "EMAIL_NOTIFICATION", emailPayload);
        emailTask.setPriority(8);
        emailTask.setScheduledAt(LocalDateTime.now().plusSeconds(5));
        
        // Create report generation task
        Map<String, Object> reportPayload = new HashMap<>();
        reportPayload.put("reportType", "MONTHLY");
        reportPayload.put("userId", 12345);
        reportPayload.put("month", "January");
        
        Task reportTask = new Task(UUID.randomUUID().toString(), "Monthly Report", "REPORT_GENERATION", reportPayload);
        reportTask.setPriority(5);
        reportTask.setScheduledAt(LocalDateTime.now().plusSeconds(10));
        
        // Create cleanup task
        Map<String, Object> cleanupPayload = new HashMap<>();
        cleanupPayload.put("dataType", "temporary_files");
        cleanupPayload.put("daysOld", 30);
        
        Task cleanupTask = new Task(UUID.randomUUID().toString(), "Cleanup Old Files", "DATA_CLEANUP", cleanupPayload);
        cleanupTask.setPriority(3);
        cleanupTask.setScheduledAt(LocalDateTime.now().plusSeconds(15));
        
        // Create backup task
        Map<String, Object> backupPayload = new HashMap<>();
        backupPayload.put("dataSource", "user_database");
        backupPayload.put("destination", "cloud_storage");
        
        Task backupTask = new Task(UUID.randomUUID().toString(), "Database Backup", "BACKUP_TASK", backupPayload);
        backupTask.setPriority(9);
        backupTask.setScheduledAt(LocalDateTime.now().plusSeconds(20));
        
        // Schedule all tasks
        taskSchedulerService.scheduleTask(emailTask);
        taskSchedulerService.scheduleTask(reportTask);
        taskSchedulerService.scheduleTask(cleanupTask);
        taskSchedulerService.scheduleTask(backupTask);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "4 sample tasks created and scheduled");
        response.put("emailTaskId", emailTask.getTaskId());
        response.put("reportTaskId", reportTask.getTaskId());
        response.put("cleanupTaskId", cleanupTask.getTaskId());
        response.put("backupTaskId", backupTask.getTaskId());
        
        return ResponseEntity.ok(response);
    }
}
