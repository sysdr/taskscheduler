package com.scheduler.controller;

import com.scheduler.model.TaskRequest;
import com.scheduler.model.TaskExecution;
import com.scheduler.service.TimeoutTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TimeoutTaskService taskService;

    @Autowired
    public TaskController(TimeoutTaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitTask(@RequestBody Map<String, Object> requestBody) {
        try {
            String taskType = (String) requestBody.get("taskType");
            Integer timeoutSeconds = (Integer) requestBody.get("timeoutSeconds");
            String payload = (String) requestBody.getOrDefault("payload", "Default payload");
            
            TaskRequest request = new TaskRequest(
                UUID.randomUUID().toString(),
                taskType,
                Duration.ofSeconds(timeoutSeconds),
                payload
            );
            
            String taskId = taskService.submitTask(request);
            
            return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", "SUBMITTED"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskExecution>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskExecution> getTask(@PathVariable String taskId) {
        TaskExecution task = taskService.getTask(taskId);
        if (task != null) {
            return ResponseEntity.ok(task);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, String>> cancelTask(@PathVariable String taskId) {
        boolean cancelled = taskService.cancelTask(taskId);
        if (cancelled) {
            return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", "CANCELLED"
            ));
        }
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Task not found or already completed"
        ));
    }
}
