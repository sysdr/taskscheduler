package com.taskscheduler.service;

import com.taskscheduler.dto.MetricsResponse;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.repository.AuditLogRepository;
import com.taskscheduler.repository.TaskExecutionRepository;
import com.taskscheduler.repository.TaskRepository;
import com.taskscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MetricsService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskExecutionRepository executionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private DemoDataService demoDataService;
    
    public MetricsResponse getUserMetrics() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new MetricsResponse(0, 0, 0, 0, 0, 0, 0, 0);
        }
        
        // Auto-create demo data if user has no tasks
        var allTasks = taskRepository.findByOwner(user);
        if (allTasks.isEmpty()) {
            demoDataService.createDemoTasksForUser(user);
            // Reload tasks after creating demo data
            allTasks = taskRepository.findByOwner(user);
        }
        
        long totalTasks = allTasks.size();
        long activeTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.ACTIVE)
                .count();
        long inactiveTasks = totalTasks - activeTasks;
        
        // Get all task IDs owned by user
        var userTaskIds = allTasks.stream().map(Task::getId).toList();
        
        if (userTaskIds.isEmpty()) {
            return new MetricsResponse(
                    totalTasks, activeTasks, inactiveTasks,
                    0, 0, 0, 1, 0
            );
        }
        
        // Get executions by iterating through tasks to avoid lazy loading issues
        long totalExecutions = 0;
        long successfulExecutions = 0;
        long failedExecutions = 0;
        long recentExecutions = 0;
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        
        for (Task task : allTasks) {
            var executions = executionRepository.findByTaskOrderByStartTimeDesc(task);
            totalExecutions += executions.size();
            
            for (TaskExecution execution : executions) {
                if (execution.getStatus() == TaskExecution.ExecutionStatus.SUCCESS) {
                    successfulExecutions++;
                } else if (execution.getStatus() == TaskExecution.ExecutionStatus.FAILED) {
                    failedExecutions++;
                }
                
                if (execution.getStartTime() != null && 
                    execution.getStartTime().isAfter(twentyFourHoursAgo)) {
                    recentExecutions++;
                }
            }
        }
        
        return new MetricsResponse(
                totalTasks, activeTasks, inactiveTasks,
                totalExecutions, successfulExecutions, failedExecutions,
                1, recentExecutions
        );
    }
    
    public MetricsResponse getAdminMetrics() {
        long totalTasks = taskRepository.count();
        long activeTasks = taskRepository.findByStatus(Task.TaskStatus.ACTIVE).size();
        long inactiveTasks = totalTasks - activeTasks;
        
        var allExecutions = executionRepository.findAll();
        long totalExecutions = allExecutions.size();
        long successfulExecutions = allExecutions.stream()
                .filter(e -> e.getStatus() == TaskExecution.ExecutionStatus.SUCCESS)
                .count();
        long failedExecutions = allExecutions.stream()
                .filter(e -> e.getStatus() == TaskExecution.ExecutionStatus.FAILED)
                .count();
        
        long totalUsers = userRepository.count();
        
        long recentExecutions = allExecutions.stream()
                .filter(e -> e.getStartTime() != null && 
                        e.getStartTime().isAfter(LocalDateTime.now().minusHours(24)))
                .count();
        
        return new MetricsResponse(
                totalTasks, activeTasks, inactiveTasks,
                totalExecutions, successfulExecutions, failedExecutions,
                totalUsers, recentExecutions
        );
    }
}

