package com.taskscheduler.consumer;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.service.TaskSchedulerService;
import com.taskscheduler.service.TaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Component
public class TaskExecutionConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionConsumer.class);
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    @Autowired
    private TaskExecutionService taskExecutionService;
    
    @KafkaListener(topics = "${app.kafka.task-execution-topic}")
    public void consumeTask(
            @Payload Task task,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        String workerId = "worker-" + Thread.currentThread().getName();
        String executionId = UUID.randomUUID().toString();
        
        logger.info("Worker {} received task: {} from partition: {}, offset: {}", 
            workerId, task.getTaskId(), partition, offset);
        
        TaskExecution execution = new TaskExecution(executionId, task.getTaskId(), workerId);
        taskExecutionService.addExecution(execution);
        
        try {
            // Update task status to processing
            taskSchedulerService.updateTaskStatus(task.getTaskId(), TaskStatus.PROCESSING);
            
            // Execute the task
            String result = executeTask(task);
            
            // Mark execution as completed
            execution.markCompleted(result);
            taskSchedulerService.updateTaskStatus(task.getTaskId(), TaskStatus.COMPLETED);
            
            logger.info("Task completed successfully: {} by worker: {}", task.getTaskId(), workerId);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Task execution failed: {} by worker: {}", task.getTaskId(), workerId, e);
            
            execution.markFailed(e.getMessage());
            
            if (task.canRetry()) {
                task.incrementRetries();
                taskSchedulerService.updateTaskStatus(task.getTaskId(), TaskStatus.RETRYING);
                logger.info("Task will be retried: {} (attempt {}/{})", 
                    task.getTaskId(), task.getCurrentRetries(), task.getMaxRetries());
                // Don't acknowledge - let Kafka retry
            } else {
                taskSchedulerService.updateTaskStatus(task.getTaskId(), TaskStatus.DEAD_LETTER);
                logger.warn("Task moved to dead letter: {} after {} attempts", 
                    task.getTaskId(), task.getCurrentRetries());
                acknowledgment.acknowledge();
            }
        }
    }
    
    private String executeTask(Task task) throws Exception {
        // Simulate different types of task execution
        switch (task.getTaskType()) {
            case "EMAIL_NOTIFICATION":
                return executeEmailTask(task);
            case "REPORT_GENERATION":
                return executeReportTask(task);
            case "DATA_CLEANUP":
                return executeCleanupTask(task);
            case "BACKUP_TASK":
                return executeBackupTask(task);
            default:
                return executeGenericTask(task);
        }
    }
    
    private String executeEmailTask(Task task) throws Exception {
        Thread.sleep(2000 + (long)(Math.random() * 3000)); // 2-5 seconds
        
        String recipient = (String) task.getPayload().get("recipient");
        String subject = (String) task.getPayload().get("subject");
        
        // Simulate occasional failures
        if (Math.random() < 0.1) {
            throw new RuntimeException("SMTP server temporarily unavailable");
        }
        
        return String.format("Email sent to %s with subject '%s'", recipient, subject);
    }
    
    private String executeReportTask(Task task) throws Exception {
        Thread.sleep(5000 + (long)(Math.random() * 10000)); // 5-15 seconds
        
        String reportType = (String) task.getPayload().get("reportType");
        Integer userId = (Integer) task.getPayload().get("userId");
        
        // Simulate occasional failures
        if (Math.random() < 0.05) {
            throw new RuntimeException("Database connection timeout");
        }
        
        return String.format("Generated %s report for user %d", reportType, userId);
    }
    
    private String executeCleanupTask(Task task) throws Exception {
        Thread.sleep(3000 + (long)(Math.random() * 7000)); // 3-10 seconds
        
        String dataType = (String) task.getPayload().get("dataType");
        Integer daysOld = (Integer) task.getPayload().get("daysOld");
        
        int recordsDeleted = (int)(Math.random() * 1000) + 100;
        
        return String.format("Cleaned up %d records of %s older than %d days", 
            recordsDeleted, dataType, daysOld);
    }
    
    private String executeBackupTask(Task task) throws Exception {
        Thread.sleep(8000 + (long)(Math.random() * 12000)); // 8-20 seconds
        
        String dataSource = (String) task.getPayload().get("dataSource");
        
        // Simulate occasional failures
        if (Math.random() < 0.08) {
            throw new RuntimeException("Storage quota exceeded");
        }
        
        long backupSize = (long)(Math.random() * 1000000000); // Random size in bytes
        return String.format("Backed up %s (%.2f MB)", dataSource, backupSize / 1024.0 / 1024.0);
    }
    
    private String executeGenericTask(Task task) throws Exception {
        Thread.sleep(1000 + (long)(Math.random() * 4000)); // 1-5 seconds
        return "Generic task executed successfully";
    }
}
