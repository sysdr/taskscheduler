package com.scheduler.consumer;

import com.scheduler.model.Task;
import com.scheduler.model.TaskPriority;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskConsumerService.class);
    
    private final MetricsService metricsService;
    private final AtomicInteger highPriorityProcessedCount;
    private final int highPriorityBatchSize;
    private final boolean starvationPreventionEnabled;
    
    public TaskConsumerService(MetricsService metricsService,
                               @Value("${scheduler.priority.high-priority-batch-size}") int highPriorityBatchSize,
                               @Value("${scheduler.priority.starvation-prevention-enabled}") boolean starvationPreventionEnabled) {
        this.metricsService = metricsService;
        this.highPriorityProcessedCount = new AtomicInteger(0);
        this.highPriorityBatchSize = highPriorityBatchSize;
        this.starvationPreventionEnabled = starvationPreventionEnabled;
    }
    
    @RabbitListener(queues = "task.queue.high")
    public void processHighPriorityTask(Task task) {
        processTask(task, TaskPriority.HIGH);
        highPriorityProcessedCount.incrementAndGet();
    }
    
    @RabbitListener(queues = "task.queue.normal")
    public void processNormalPriorityTask(Task task) {
        processTask(task, TaskPriority.NORMAL);
    }
    
    @RabbitListener(queues = "task.queue.low")
    public void processLowPriorityTask(Task task) {
        // Implement starvation prevention
        if (starvationPreventionEnabled && 
            highPriorityProcessedCount.get() >= highPriorityBatchSize) {
            logger.info("Starvation prevention: Processing low-priority task after {} high-priority tasks",
                    highPriorityBatchSize);
            highPriorityProcessedCount.set(0);
        }
        processTask(task, TaskPriority.LOW);
    }
    
    private void processTask(Task task, TaskPriority priority) {
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartedAt(startTime);
        task.setStatus(TaskStatus.PROCESSING);
        
        logger.info("Processing {} priority task: {} (ID: {})", 
                priority, task.getName(), task.getId());
        
        try {
            // Simulate task processing with different durations based on priority
            int processingTime = switch (priority) {
                case HIGH -> 500;  // Fast processing for high priority
                case NORMAL -> 1000;
                case LOW -> 2000;  // Slower processing for low priority
            };
            
            Thread.sleep(processingTime);
            
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            
            metricsService.recordProcessingTime(
                    priority.name(), 
                    startTime, 
                    task.getCompletedAt()
            );
            
            logger.info("Completed {} priority task: {} in {}ms", 
                    priority, task.getName(), 
                    java.time.Duration.between(startTime, task.getCompletedAt()).toMillis());
            
        } catch (InterruptedException e) {
            task.setStatus(TaskStatus.FAILED);
            logger.error("Failed to process task: {}", task.getId(), e);
            Thread.currentThread().interrupt();
        }
    }
}
