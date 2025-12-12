package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    
    @Value("${HOSTNAME:unknown}")
    private String podName;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
        Task task = taskService.createTask(
            request.getName(),
            request.getDescription(),
            request.getScheduledTime()
        );
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("pod", podName);
        health.put("tasksProcessed", taskService.getTasksProcessedCount());
        health.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(health);
    }
    
    static class TaskRequest {
        private String name;
        private String description;
        private LocalDateTime scheduledTime;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    }
}
