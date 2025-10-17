package com.taskscheduler.controller;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskResult;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.model.TaskType;
import com.taskscheduler.service.TaskExecutionService;
import com.taskscheduler.service.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskExecutionService taskExecutionService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
        Task task = new Task(request.getName(), request.getDescription(), request.getType());
        task.setScheduledAt(request.getScheduledAt() != null ? 
                           request.getScheduledAt() : LocalDateTime.now());
        task.setMaxRetries(request.getMaxRetries() != null ? 
                          request.getMaxRetries() : 3);
        
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<String> executeTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Task task = taskOpt.get();
        CompletableFuture<TaskResult> future = taskExecutionService.executeTask(task);
        
        return ResponseEntity.ok("Task execution started for ID: " + id);
    }
    
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public List<Task> getTasksByStatus(@PathVariable TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    @GetMapping("/retryable")
    public List<Task> getRetryableTasks() {
        return taskRepository.findRetryableTasks();
    }
    
    @GetMapping("/dead-letter")
    public List<Task> getDeadLetterTasks() {
        return taskRepository.findDeadLetterTasks();
    }
    
    public static class TaskRequest {
        private String name;
        private String description;
        private TaskType type;
        private LocalDateTime scheduledAt;
        private Integer maxRetries;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public TaskType getType() { return type; }
        public void setType(TaskType type) { this.type = type; }
        
        public LocalDateTime getScheduledAt() { return scheduledAt; }
        public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
        
        public Integer getMaxRetries() { return maxRetries; }
        public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    }
}
