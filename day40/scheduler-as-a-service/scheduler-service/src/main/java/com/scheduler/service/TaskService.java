package com.scheduler.service;

import com.scheduler.model.*;
import com.scheduler.repository.TaskRepository;
import com.scheduler.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private CallbackService callbackService;
    
    @Transactional
    public Task submitTask(String tenantId, TaskSubmissionRequest request) {
        // Validate tenant
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));
        
        // Check concurrent task limit
        long runningTasks = taskRepository.countByTenantIdAndStatus(tenantId, TaskStatus.RUNNING);
        if (runningTasks >= tenant.getMaxConcurrentTasks()) {
            throw new RuntimeException("Tenant concurrent task limit reached");
        }
        
        // Create task
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTenantId(tenantId);
        task.setTaskName(request.getTaskName());
        task.setPayload(request.getPayload());
        task.setCallbackUrl(request.getCallbackUrl());
        
        if (request.getScheduledFor() != null) {
            task.setScheduledFor(request.getScheduledFor());
            task.setStatus(TaskStatus.SCHEDULED);
        } else {
            task.setScheduledFor(LocalDateTime.now());
            task.setStatus(TaskStatus.PENDING);
        }
        
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        
        if (request.getMaxRetries() != null) {
            task.setMaxRetries(request.getMaxRetries());
        }
        
        return taskRepository.save(task);
    }
    
    public Task getTask(String taskId) {
        return taskRepository.findByTaskId(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    public List<Task> getTasksByTenant(String tenantId) {
        return taskRepository.findByTenantId(tenantId);
    }
    
    public List<Task> getTasksByTenantAndStatus(String tenantId, TaskStatus status) {
        return taskRepository.findByTenantIdAndStatus(tenantId, status);
    }
    
    @Transactional
    public void cancelTask(String taskId) {
        Task task = getTask(taskId);
        if (task.getStatus() == TaskStatus.RUNNING) {
            throw new RuntimeException("Cannot cancel running task");
        }
        task.setStatus(TaskStatus.CANCELLED);
        taskRepository.save(task);
    }
    
    @Transactional
    public void executeTask(Task task) {
        task.setStatus(TaskStatus.RUNNING);
        task.setStartedAt(LocalDateTime.now());
        taskRepository.save(task);
        
        try {
            // Simulate task execution
            Thread.sleep(2000);
            String result = "Task executed successfully: " + task.getTaskName();
            
            task.setStatus(TaskStatus.COMPLETED);
            task.setResult(result);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            // Send callback
            if (task.getCallbackUrl() != null) {
                callbackService.sendCallback(task);
            }
            
        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());
            task.setRetryCount(task.getRetryCount() + 1);
            taskRepository.save(task);
            
            // Retry logic
            if (task.getRetryCount() < task.getMaxRetries()) {
                task.setStatus(TaskStatus.SCHEDULED);
                task.setScheduledFor(LocalDateTime.now().plusMinutes(5));
                taskRepository.save(task);
            }
        }
    }
}
