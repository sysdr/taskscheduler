package com.taskscheduler.controller;

import com.taskscheduler.dto.TaskDto;
import com.taskscheduler.entity.Task;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskDto taskDto) {
        logger.info("Creating task via API: {}", taskDto.getName());
        Task createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        logger.debug("Fetching task via API: {}", id);
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        logger.debug("Fetching tasks by status via API: {}", status);
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<Map<String, String>> executeTask(@PathVariable Long id) {
        logger.info("Executing task via API: {}", id);
        
        CompletableFuture<Void> future = taskService.executeTaskAsync(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task execution started");
        response.put("taskId", id.toString());
        response.put("status", "RUNNING");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTaskStats() {
        Map<String, Object> stats = new HashMap<>();
        
        for (TaskStatus status : TaskStatus.values()) {
            stats.put(status.name().toLowerCase() + "Count", taskService.getTaskCountByStatus(status));
        }
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/cleanup-stuck")
    public ResponseEntity<Map<String, String>> cleanupStuckTasks(
            @RequestParam(defaultValue = "30") int timeoutMinutes) {
        
        logger.info("Cleaning up stuck tasks with timeout: {} minutes", timeoutMinutes);
        taskService.cleanupStuckTasks(timeoutMinutes);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Stuck tasks cleanup completed");
        response.put("timeoutMinutes", String.valueOf(timeoutMinutes));
        
        return ResponseEntity.ok(response);
    }
}
