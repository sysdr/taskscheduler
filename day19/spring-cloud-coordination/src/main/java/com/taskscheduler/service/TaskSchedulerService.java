package com.taskscheduler.service;

import com.taskscheduler.model.ScheduledTask;
import com.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TaskSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CoordinationService coordinationService;
    
    @Value("${spring.application.name:unknown}")
    private String instanceId;
    
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    
    @EventListener
    public void onLeadershipGranted(OnGrantedEvent event) {
        logger.info("Leadership granted to instance: {}", instanceId);
        isLeader.set(true);
    }
    
    @EventListener
    public void onLeadershipRevoked(OnRevokedEvent event) {
        logger.info("Leadership revoked from instance: {}", instanceId);
        isLeader.set(false);
    }
    
    @Scheduled(fixedDelay = 5000) // Check every 5 seconds
    public void processPendingTasks() {
        if (!isLeader.get()) {
            logger.debug("Not a leader, skipping task processing");
            return;
        }
        
        try {
            coordinationService.executeWithLock(
                "task-processing", 
                10, 
                TimeUnit.SECONDS,
                () -> {
                    List<ScheduledTask> pendingTasks = taskRepository
                        .findPendingTasksBeforeTime(LocalDateTime.now());
                    
                    logger.info("Found {} pending tasks to process", pendingTasks.size());
                    
                    for (ScheduledTask task : pendingTasks) {
                        processTask(task);
                    }
                }
            );
        } catch (Exception e) {
            logger.error("Error processing pending tasks", e);
        }
    }
    
    private void processTask(ScheduledTask task) {
        String taskLockKey = "task-execution-" + task.getTaskId();
        
        boolean executed = coordinationService.tryExecuteWithLock(taskLockKey, () -> {
            try {
                logger.info("Executing task: {} of type: {}", task.getTaskId(), task.getTaskType());
                
                // Update task status
                task.setStatus("RUNNING");
                task.setExecutorInstance(instanceId);
                task.setExecutedTime(LocalDateTime.now());
                taskRepository.save(task);
                
                // Simulate task execution
                executeTaskLogic(task);
                
                // Mark as completed
                task.setStatus("COMPLETED");
                taskRepository.save(task);
                
                logger.info("Completed task: {}", task.getTaskId());
                
            } catch (Exception e) {
                logger.error("Error executing task: " + task.getTaskId(), e);
                task.setStatus("FAILED");
                task.setErrorMessage(e.getMessage());
                task.setRetryCount(task.getRetryCount() + 1);
                taskRepository.save(task);
            }
        });
        
        if (!executed) {
            logger.debug("Task {} is being processed by another instance", task.getTaskId());
        }
    }
    
    private void executeTaskLogic(ScheduledTask task) throws InterruptedException {
        // Simulate different task types
        switch (task.getTaskType()) {
            case "EMAIL":
                Thread.sleep(1000); // Simulate email sending
                logger.info("Email task executed: {}", task.getPayload());
                break;
            case "REPORT":
                Thread.sleep(3000); // Simulate report generation
                logger.info("Report task executed: {}", task.getPayload());
                break;
            case "BATCH":
                Thread.sleep(2000); // Simulate batch processing
                logger.info("Batch task executed: {}", task.getPayload());
                break;
            default:
                Thread.sleep(500); // Default task
                logger.info("Default task executed: {}", task.getPayload());
        }
    }
    
    public ScheduledTask scheduleTask(String taskType, String payload, LocalDateTime scheduledTime) {
        ScheduledTask task = new ScheduledTask(
            "task-" + System.currentTimeMillis(),
            taskType,
            "PENDING",
            payload,
            scheduledTime
        );
        
        return taskRepository.save(task);
    }
    
    public List<ScheduledTask> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public boolean isCurrentlyLeader() {
        return isLeader.get();
    }
}
