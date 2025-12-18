package com.scheduler.controller;

import com.scheduler.config.TenantContext;
import com.scheduler.model.ScheduledTask;
import com.scheduler.repository.ScheduledTaskRepository;
import com.scheduler.service.ResourceGovernor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final ScheduledTaskRepository taskRepository;
    private final ResourceGovernor resourceGovernor;
    
    public TaskController(ScheduledTaskRepository taskRepository, ResourceGovernor resourceGovernor) {
        this.taskRepository = taskRepository;
        this.resourceGovernor = resourceGovernor;
    }
    
    @GetMapping
    public List<ScheduledTask> getAllTasks() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return List.of();
        }
        return taskRepository.findByTenantId(tenantId);
    }
    
    @PostMapping
    public ResponseEntity<ScheduledTask> createTask(@RequestBody ScheduledTask task) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        task.setTenantId(tenantId);
        task.setStatus(ScheduledTask.TaskStatus.PENDING);
        task.setNextRunTime(LocalDateTime.now().plusMinutes(1));
        
        ScheduledTask saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resourceGovernor.getMetrics(tenantId));
    }
}
