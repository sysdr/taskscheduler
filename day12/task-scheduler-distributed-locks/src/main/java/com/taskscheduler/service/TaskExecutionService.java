package com.taskscheduler.service;

import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.repository.TaskExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TaskExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionService.class);
    
    private final DistributedLockService lockService;
    private final TaskExecutionRepository executionRepository;
    private final String instanceId;
    
    public TaskExecutionService(DistributedLockService lockService, 
                               TaskExecutionRepository executionRepository) {
        this.lockService = lockService;
        this.executionRepository = executionRepository;
        this.instanceId = "exec-" + System.currentTimeMillis();
    }
    
    /**
     * Execute a task with distributed lock protection
     */
    @Transactional
    public TaskExecution executeTask(String taskKey, String taskType) {
        String lockKey = "task-lock:" + taskKey;
        Duration lockDuration = Duration.ofMinutes(5);
        Duration waitTimeout = Duration.ofSeconds(10);
        
        logger.info("Attempting to execute task: {} with lock: {}", taskKey, lockKey);
        
        try {
            return lockService.executeWithLock(lockKey, lockDuration, waitTimeout, () -> {
                return doExecuteTask(taskKey, taskType);
            });
        } catch (Exception e) {
            logger.error("Failed to execute task: {}", taskKey, e);
            return createFailedExecution(taskKey, e.getMessage());
        }
    }
    
    private TaskExecution doExecuteTask(String taskKey, String taskType) {
        logger.info("Executing task: {} on instance: {}", taskKey, instanceId);
        
        TaskExecution execution = new TaskExecution(taskKey, instanceId);
        execution = executionRepository.save(execution);
        
        try {
            // Simulate task execution with variable duration
            String result = performTaskWork(taskType);
            
            execution.markCompleted(result);
            logger.info("Task completed successfully: {}", taskKey);
            
        } catch (Exception e) {
            execution.markFailed(e.getMessage());
            logger.error("Task failed: {}", taskKey, e);
            throw e;
        }
        
        return executionRepository.save(execution);
    }
    
    private String performTaskWork(String taskType) {
        // Simulate different types of work
        switch (taskType.toLowerCase()) {
            case "data-processing":
                return simulateDataProcessing();
            case "report-generation":
                return simulateReportGeneration();
            case "email-sending":
                return simulateEmailSending();
            case "database-cleanup":
                return simulateDatabaseCleanup();
            default:
                return simulateGenericTask();
        }
    }
    
    private String simulateDataProcessing() {
        simulateWork(2000, 5000);
        int recordsProcessed = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "Processed " + recordsProcessed + " data records";
    }
    
    private String simulateReportGeneration() {
        simulateWork(3000, 8000);
        return "Generated monthly report with 247 pages";
    }
    
    private String simulateEmailSending() {
        simulateWork(1000, 3000);
        int emailsSent = ThreadLocalRandom.current().nextInt(50, 500);
        return "Sent " + emailsSent + " notification emails";
    }
    
    private String simulateDatabaseCleanup() {
        simulateWork(5000, 12000);
        int recordsDeleted = ThreadLocalRandom.current().nextInt(100, 1000);
        return "Cleaned up " + recordsDeleted + " old records";
    }
    
    private String simulateGenericTask() {
        simulateWork(1500, 4000);
        return "Generic task completed successfully";
    }
    
    private void simulateWork(int minMs, int maxMs) {
        try {
            int duration = ThreadLocalRandom.current().nextInt(minMs, maxMs);
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task interrupted", e);
        }
    }
    
    private TaskExecution createFailedExecution(String taskKey, String errorMessage) {
        TaskExecution execution = new TaskExecution(taskKey, instanceId);
        execution.markFailed(errorMessage);
        return executionRepository.save(execution);
    }
    
    // Query methods
    public List<TaskExecution> getExecutionHistory(String taskKey) {
        return executionRepository.findByTaskKey(taskKey);
    }
    
    public List<TaskExecution> getRunningTasks() {
        return executionRepository.findByStatus(TaskExecution.ExecutionStatus.RUNNING);
    }
    
    public long getCompletedTaskCount() {
        return executionRepository.countByStatus(TaskExecution.ExecutionStatus.COMPLETED);
    }
    
    public Double getAverageExecutionTime(String taskKey) {
        return executionRepository.getAverageDurationForTask(taskKey);
    }
}
