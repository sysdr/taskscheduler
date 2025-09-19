package com.taskscheduler.controller;

import com.taskscheduler.dto.ExecutionStatsDto;
import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.service.TaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executions")
@CrossOrigin(origins = "*")
public class TaskExecutionController {
    
    @Autowired
    private TaskExecutionService executionService;
    
    @PostMapping
    public ResponseEntity<TaskExecution> createExecution(@RequestBody TaskExecutionRequest request) {
        TaskExecution execution = executionService.createExecution(request.getTaskName(), request.getTaskDescription());
        return ResponseEntity.ok(execution);
    }
    
    @GetMapping("/{executionId}")
    public ResponseEntity<TaskExecution> getExecution(@PathVariable String executionId) {
        return executionService.findByExecutionId(executionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{executionId}/start")
    public ResponseEntity<TaskExecution> startExecution(@PathVariable String executionId) {
        try {
            TaskExecution execution = executionService.startExecution(executionId);
            return ResponseEntity.ok(execution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{executionId}/complete")
    public ResponseEntity<TaskExecution> completeExecution(@PathVariable String executionId) {
        try {
            TaskExecution execution = executionService.completeExecution(executionId);
            return ResponseEntity.ok(execution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{executionId}/fail")
    public ResponseEntity<TaskExecution> failExecution(@PathVariable String executionId, 
                                                       @RequestBody TaskFailureRequest request) {
        try {
            TaskExecution execution = executionService.failExecution(executionId, 
                    request.getErrorMessage(), request.getStackTrace());
            return ResponseEntity.ok(execution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ExecutionStatsDto> getExecutionStats() {
        ExecutionStatsDto stats = executionService.getExecutionStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/failed")
    public ResponseEntity<Page<TaskExecution>> getFailedExecutions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskExecution> failedExecutions = executionService.getFailedExecutions(page, size);
        return ResponseEntity.ok(failedExecutions);
    }
    
    @GetMapping("/longest-running")
    public ResponseEntity<Page<TaskExecution>> getLongestRunningTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskExecution> longestRunning = executionService.getLongestRunningTasks(page, size);
        return ResponseEntity.ok(longestRunning);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<TaskExecution>> getRecentExecutions() {
        List<TaskExecution> recentExecutions = executionService.getRecentExecutions();
        return ResponseEntity.ok(recentExecutions);
    }
    
    // Request DTOs
    public static class TaskExecutionRequest {
        private String taskName;
        private String taskDescription;
        
        public String getTaskName() { return taskName; }
        public void setTaskName(String taskName) { this.taskName = taskName; }
        
        public String getTaskDescription() { return taskDescription; }
        public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    }
    
    public static class TaskFailureRequest {
        private String errorMessage;
        private String stackTrace;
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getStackTrace() { return stackTrace; }
        public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
    }
}
