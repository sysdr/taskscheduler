package com.scheduler.controller;

import com.scheduler.dto.TaskDto;
import com.scheduler.entity.Task;
import com.scheduler.service.TaskManagementService;
import com.scheduler.service.TaskProcessingService;
import com.scheduler.repository.TaskRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TaskManagementService taskManagementService;
    private final TaskProcessingService taskProcessingService;
    private final TaskRepository taskRepository;
    
    public TaskController(TaskManagementService taskManagementService, TaskProcessingService taskProcessingService, TaskRepository taskRepository) {
        this.taskManagementService = taskManagementService;
        this.taskProcessingService = taskProcessingService;
        this.taskRepository = taskRepository;
    }
    
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime) {
        
        TaskDto task = taskManagementService.createTask(name, description, scheduledTime);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return taskManagementService.getTask(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskManagementService.getAllTasks());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskManagementService.getTasksByStatus(status));
    }
    
    @GetMapping("/stuck")
    public ResponseEntity<List<TaskDto>> getStuckTasks(
            @RequestParam(defaultValue = "30") int timeoutMinutes) {
        return ResponseEntity.ok(taskManagementService.getStuckTasks(timeoutMinutes));
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<TaskManagementService.TaskStatistics> getStatistics() {
        return ResponseEntity.ok(taskManagementService.getStatistics());
    }
    
    @PostMapping("/trigger-processing")
    public ResponseEntity<String> triggerTaskProcessing() {
        taskProcessingService.processPendingTasks();
        return ResponseEntity.ok("Task processing triggered manually");
    }
    
    @GetMapping("/debug/available-tasks")
    public ResponseEntity<String> debugAvailableTasks() {
        // This will help us see what tasks are being found by the query
        return ResponseEntity.ok("Debug endpoint - check logs for available tasks");
    }
    
    @GetMapping("/debug/tasks-info")
    public ResponseEntity<String> debugTasksInfo() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> allTasks = taskRepository.findAll();
        List<Task> availableTasks = taskRepository.findAvailableTasksForProcessing(now, org.springframework.data.domain.PageRequest.of(0, 10));
        
        StringBuilder info = new StringBuilder();
        info.append("Current time: ").append(now).append("\n");
        info.append("Total tasks: ").append(allTasks.size()).append("\n");
        info.append("Available tasks: ").append(availableTasks.size()).append("\n\n");
        
        info.append("All tasks:\n");
        for (Task task : allTasks) {
            info.append("ID: ").append(task.getId())
                .append(", Name: ").append(task.getName())
                .append(", Status: ").append(task.getStatus())
                .append(", Scheduled: ").append(task.getScheduledTime())
                .append(", RetryCount: ").append(task.getRetryCount())
                .append(", MaxRetries: ").append(task.getMaxRetries())
                .append(", Can be processed: ").append(task.canBeProcessed())
                .append(", Version: ").append(task.getVersion())
                .append("\n");
        }
        
        if (!availableTasks.isEmpty()) {
            info.append("\nAvailable tasks details:\n");
            for (Task task : availableTasks) {
                info.append("ID: ").append(task.getId())
                    .append(", Name: ").append(task.getName())
                    .append(", Status: ").append(task.getStatus())
                    .append(", Version: ").append(task.getVersion())
                    .append("\n");
            }
        }
        
        return ResponseEntity.ok(info.toString());
    }
}
