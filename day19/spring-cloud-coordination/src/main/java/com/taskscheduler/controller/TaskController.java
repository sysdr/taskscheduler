package com.taskscheduler.controller;

import com.taskscheduler.model.ScheduledTask;
import com.taskscheduler.service.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    @PostMapping
    public ResponseEntity<ScheduledTask> scheduleTask(@RequestBody Map<String, Object> request) {
        String taskType = (String) request.get("taskType");
        String payload = (String) request.get("payload");
        
        // Default to current time if no scheduled time provided
        LocalDateTime scheduledTime;
        if (request.containsKey("scheduledTime")) {
            String scheduledTimeStr = (String) request.get("scheduledTime");
            try {
                // Handle ISO format from JavaScript (remove Z and milliseconds if present)
                String cleanTimeStr = scheduledTimeStr.replace("Z", "").replaceAll("\\.\\d{3}", "");
                scheduledTime = LocalDateTime.parse(cleanTimeStr);
            } catch (Exception e) {
                // Fallback to current time + 10 seconds if parsing fails
                scheduledTime = LocalDateTime.now().plusSeconds(10);
            }
        } else {
            scheduledTime = LocalDateTime.now().plusSeconds(10);
        }
        
        ScheduledTask task = taskSchedulerService.scheduleTask(taskType, payload, scheduledTime);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<ScheduledTask>> getAllTasks() {
        List<ScheduledTask> tasks = taskSchedulerService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        return ResponseEntity.ok(Map.of(
            "isLeader", taskSchedulerService.isCurrentlyLeader(),
            "timestamp", LocalDateTime.now()
        ));
    }
}
