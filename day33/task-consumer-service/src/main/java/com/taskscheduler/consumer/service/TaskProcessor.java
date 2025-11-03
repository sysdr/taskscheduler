package com.taskscheduler.consumer.service;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.TaskExecutionRequest;
import com.taskscheduler.consumer.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private StatusPublisher statusPublisher;
    
    private final String workerId = "worker-" + UUID.randomUUID().toString().substring(0, 8);
    
    @Async
    public CompletableFuture<Void> processTask(TaskExecutionRequest request) {
        logger.info("Worker {} received task: {}", workerId, request.getTaskId());
        
        // Find or create task record
        Task task = taskRepository.findByTaskId(request.getTaskId())
                .orElse(new Task(request.getTaskId(), request.getTaskType(), request.getPayload()));
        
        // Mark as processing
        task.setStatus(Task.TaskStatus.PROCESSING);
        task.setWorkerId(workerId);
        task.setStartTime(LocalDateTime.now());
        task = taskRepository.save(task);
        
        // Publish status update
        statusPublisher.publishStatus(task.getTaskId(), "PROCESSING", workerId, null);
        
        try {
            // Execute the actual task based on type
            executeTaskByType(request);
            
            // Mark as completed
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompletedTime(LocalDateTime.now());
            task = taskRepository.save(task);
            
            statusPublisher.publishStatus(task.getTaskId(), "COMPLETED", workerId, null);
            logger.info("Worker {} completed task: {}", workerId, request.getTaskId());
            
        } catch (Exception e) {
            logger.error("Worker {} failed to process task: {}", workerId, request.getTaskId(), e);
            
            task.setStatus(Task.TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setRetryCount(task.getRetryCount() + 1);
            task = taskRepository.save(task);
            
            statusPublisher.publishStatus(task.getTaskId(), "FAILED", workerId, e.getMessage());
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    private void executeTaskByType(TaskExecutionRequest request) throws Exception {
        switch (request.getTaskType().toLowerCase()) {
            case "email":
                executeEmailTask(request);
                break;
            case "notification":
                executeNotificationTask(request);
                break;
            case "report":
                executeReportTask(request);
                break;
            case "backup":
                executeBackupTask(request);
                break;
            case "cleanup":
                executeCleanupTask(request);
                break;
            default:
                executeGenericTask(request);
        }
    }
    
    private void executeEmailTask(TaskExecutionRequest request) throws Exception {
        logger.info("Sending email for task: {}", request.getTaskId());
        Thread.sleep(2000 + (int)(Math.random() * 3000)); // Simulate email sending
        logger.info("Email sent successfully for task: {}", request.getTaskId());
    }
    
    private void executeNotificationTask(TaskExecutionRequest request) throws Exception {
        logger.info("Sending notification for task: {}", request.getTaskId());
        Thread.sleep(1000 + (int)(Math.random() * 2000)); // Simulate notification
        logger.info("Notification sent successfully for task: {}", request.getTaskId());
    }
    
    private void executeReportTask(TaskExecutionRequest request) throws Exception {
        logger.info("Generating report for task: {}", request.getTaskId());
        Thread.sleep(5000 + (int)(Math.random() * 5000)); // Simulate report generation
        logger.info("Report generated successfully for task: {}", request.getTaskId());
    }
    
    private void executeBackupTask(TaskExecutionRequest request) throws Exception {
        logger.info("Running backup for task: {}", request.getTaskId());
        Thread.sleep(3000 + (int)(Math.random() * 4000)); // Simulate backup
        logger.info("Backup completed successfully for task: {}", request.getTaskId());
    }
    
    private void executeCleanupTask(TaskExecutionRequest request) throws Exception {
        logger.info("Running cleanup for task: {}", request.getTaskId());
        Thread.sleep(1500 + (int)(Math.random() * 2500)); // Simulate cleanup
        logger.info("Cleanup completed successfully for task: {}", request.getTaskId());
    }
    
    private void executeGenericTask(TaskExecutionRequest request) throws Exception {
        logger.info("Executing generic task: {}", request.getTaskId());
        Thread.sleep(2000 + (int)(Math.random() * 3000)); // Simulate work
        logger.info("Generic task completed successfully for task: {}", request.getTaskId());
    }
    
    public String getWorkerId() {
        return workerId;
    }
}
