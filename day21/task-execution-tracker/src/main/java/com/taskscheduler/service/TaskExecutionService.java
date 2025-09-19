package com.taskscheduler.service;

import com.taskscheduler.dto.ExecutionStatsDto;
import com.taskscheduler.entity.ExecutionStatus;
import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.repository.TaskExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskExecutionService {
    
    @Autowired
    private TaskExecutionRepository repository;
    
    public TaskExecution createExecution(String taskName, String taskDescription) {
        TaskExecution execution = new TaskExecution(taskName, taskDescription);
        return repository.save(execution);
    }
    
    public Optional<TaskExecution> findByExecutionId(String executionId) {
        return repository.findByExecutionId(executionId);
    }
    
    public TaskExecution startExecution(String executionId) {
        Optional<TaskExecution> optionalExecution = repository.findByExecutionId(executionId);
        if (optionalExecution.isPresent()) {
            TaskExecution execution = optionalExecution.get();
            try {
                String nodeId = InetAddress.getLocalHost().getHostName();
                execution.markAsRunning(nodeId);
                return repository.save(execution);
            } catch (Exception e) {
                execution.markAsRunning("unknown-node");
                return repository.save(execution);
            }
        }
        throw new RuntimeException("Execution not found: " + executionId);
    }
    
    public TaskExecution completeExecution(String executionId) {
        Optional<TaskExecution> optionalExecution = repository.findByExecutionId(executionId);
        if (optionalExecution.isPresent()) {
            TaskExecution execution = optionalExecution.get();
            execution.markAsCompleted();
            return repository.save(execution);
        }
        throw new RuntimeException("Execution not found: " + executionId);
    }
    
    public TaskExecution failExecution(String executionId, String errorMessage, String stackTrace) {
        Optional<TaskExecution> optionalExecution = repository.findByExecutionId(executionId);
        if (optionalExecution.isPresent()) {
            TaskExecution execution = optionalExecution.get();
            execution.markAsFailed(errorMessage, stackTrace);
            return repository.save(execution);
        }
        throw new RuntimeException("Execution not found: " + executionId);
    }
    
    public ExecutionStatsDto getExecutionStats() {
        ExecutionStatsDto stats = new ExecutionStatsDto();
        stats.setPendingCount(repository.countByStatus(ExecutionStatus.PENDING));
        stats.setRunningCount(repository.countByStatus(ExecutionStatus.RUNNING));
        stats.setSuccessCount(repository.countByStatus(ExecutionStatus.SUCCESS));
        stats.setFailedCount(repository.countByStatus(ExecutionStatus.FAILED));
        
        Double avgDuration = repository.getAverageExecutionTime();
        stats.setAverageExecutionTimeMs(avgDuration != null ? avgDuration : 0.0);
        
        long totalExecutions = stats.getPendingCount() + stats.getRunningCount() + 
                              stats.getSuccessCount() + stats.getFailedCount();
        
        if (totalExecutions > 0) {
            double successRate = ((double) stats.getSuccessCount() / totalExecutions) * 100;
            stats.setSuccessRate(successRate);
        }
        
        return stats;
    }
    
    public Page<TaskExecution> getFailedExecutions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findFailedExecutions(pageable);
    }
    
    public Page<TaskExecution> getLongestRunningTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findLongestRunningTasks(pageable);
    }
    
    public List<TaskExecution> getRecentExecutions() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime now = LocalDateTime.now();
        return repository.findByTimeRange(oneHourAgo, now);
    }
}
