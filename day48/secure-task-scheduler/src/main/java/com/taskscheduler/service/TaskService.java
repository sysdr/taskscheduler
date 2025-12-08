package com.taskscheduler.service;

import com.taskscheduler.dto.TaskRequest;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.model.User;
import com.taskscheduler.repository.TaskExecutionRepository;
import com.taskscheduler.repository.TaskRepository;
import com.taskscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskExecutionRepository executionRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Transactional
    public Task createTask(TaskRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setCronExpression(request.getCronExpression());
        task.setOwner(owner);
        task.setStatus(Task.TaskStatus.INACTIVE);
        
        Task savedTask = taskRepository.save(task);
        
        auditService.logAction(username, "TASK_CREATED", 
                "TASK", savedTask.getId(), "Created task: " + task.getName());
        
        return savedTask;
    }
    
    public List<Task> getMyTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return taskRepository.findByOwner(owner);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @Transactional
    public Task updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (!task.getOwner().getUsername().equals(username) &&
                !SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Unauthorized to update this task");
        }
        
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setCronExpression(request.getCronExpression());
        
        Task updated = taskRepository.save(task);
        
        auditService.logAction(username, "TASK_UPDATED", 
                "TASK", id, "Updated task: " + task.getName());
        
        return updated;
    }
    
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Authentication required");
        }
        
        String username = authentication.getName();
        
        // Check authorization - owner can delete, admin can delete any task
        String ownerUsername;
        try {
            ownerUsername = task.getOwner().getUsername();
        } catch (Exception e) {
            throw new RuntimeException("Unable to access task owner: " + e.getMessage());
        }
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!ownerUsername.equals(username) && !isAdmin) {
            throw new RuntimeException("Unauthorized to delete this task. Only the task owner or admin can delete it.");
        }
        
        // Delete all executions first to avoid foreign key constraints
        var executions = executionRepository.findByTaskOrderByStartTimeDesc(task);
        executionRepository.deleteAll(executions);
        
        // Delete the task
        taskRepository.delete(task);
        
        auditService.logAction(username, "TASK_DELETED", 
                "TASK", id, "Deleted task: " + task.getName());
    }
    
    @Transactional
    public void executeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        TaskExecution execution = new TaskExecution();
        execution.setTask(task);
        execution.setStartTime(LocalDateTime.now());
        execution.setStatus(TaskExecution.ExecutionStatus.RUNNING);
        
        try {
            // Simulate task execution
            Thread.sleep(1000);
            
            execution.setEndTime(LocalDateTime.now());
            execution.setStatus(TaskExecution.ExecutionStatus.SUCCESS);
            execution.setResult("Task executed successfully");
            execution.setDurationMs(1000L);
            
            task.setLastExecuted(LocalDateTime.now());
            task.setExecutionCount(task.getExecutionCount() + 1);
            
        } catch (Exception e) {
            execution.setEndTime(LocalDateTime.now());
            execution.setStatus(TaskExecution.ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
        }
        
        executionRepository.save(execution);
        taskRepository.save(task);
        
        auditService.logAction(task.getOwner().getUsername(), "TASK_EXECUTED",
                "TASK", taskId, "Task execution: " + execution.getStatus());
    }
    
    public List<TaskExecution> getTaskExecutions(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (!task.getOwner().getUsername().equals(username) &&
                !SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Unauthorized to view executions");
        }
        
        return executionRepository.findTop10ByTaskOrderByStartTimeDesc(task);
    }
    
    @Transactional
    public Task toggleTaskStatus(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Authentication required");
        }
        
        String username = authentication.getName();
        
        // Check authorization - same pattern as updateTask
        String ownerUsername;
        try {
            ownerUsername = task.getOwner().getUsername();
        } catch (Exception e) {
            throw new RuntimeException("Unable to access task owner: " + e.getMessage());
        }
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!ownerUsername.equals(username) && !isAdmin) {
            throw new RuntimeException("Unauthorized to update this task");
        }
        
        // Toggle between ACTIVE and INACTIVE, or set FAILED/COMPLETED to ACTIVE
        Task.TaskStatus currentStatus = task.getStatus();
        if (currentStatus == Task.TaskStatus.ACTIVE) {
            task.setStatus(Task.TaskStatus.INACTIVE);
        } else if (currentStatus == Task.TaskStatus.INACTIVE) {
            task.setStatus(Task.TaskStatus.ACTIVE);
        } else if (currentStatus == Task.TaskStatus.FAILED || 
                   currentStatus == Task.TaskStatus.COMPLETED) {
            // Allow reactivating failed or completed tasks
            task.setStatus(Task.TaskStatus.ACTIVE);
        }
        
        Task updated = taskRepository.save(task);
        
        auditService.logAction(username, "TASK_STATUS_TOGGLED", 
                "TASK", taskId, "Task status changed from " + currentStatus + " to " + updated.getStatus());
        
        return updated;
    }
}
