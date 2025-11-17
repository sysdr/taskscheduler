package com.scheduler.controller;

import com.scheduler.model.*;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<Task> submitTask(
            @RequestBody TaskSubmissionRequest request,
            Authentication authentication) {
        String tenantId = authentication.getName();
        Task task = taskService.submitTask(tenantId, request);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        Task task = taskService.getTask(taskId);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            @RequestParam(required = false) String status,
            Authentication authentication) {
        String tenantId = authentication.getName();
        
        if (status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(taskService.getTasksByTenantAndStatus(tenantId, taskStatus));
        }
        
        return ResponseEntity.ok(taskService.getTasksByTenant(tenantId));
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> cancelTask(@PathVariable String taskId) {
        taskService.cancelTask(taskId);
        return ResponseEntity.ok().build();
    }
}
