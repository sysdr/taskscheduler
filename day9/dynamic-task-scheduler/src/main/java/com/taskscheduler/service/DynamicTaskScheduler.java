package com.taskscheduler.service;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.repository.TaskDefinitionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicTaskScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicTaskScheduler.class);
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    @Autowired
    private TaskDefinitionRepository taskRepository;
    
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initializeScheduler() {
        logger.info("🚀 Initializing Dynamic Task Scheduler...");
        loadAndScheduleActiveTasks();
    }
    
    @PreDestroy
    public void cleanup() {
        logger.info("🛑 Shutting down Dynamic Task Scheduler...");
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
    }
    
    public void loadAndScheduleActiveTasks() {
        List<TaskDefinition> activeTasks = taskRepository.findActiveTasks();
        logger.info("📋 Loading {} active tasks from database", activeTasks.size());
        
        for (TaskDefinition task : activeTasks) {
            try {
                scheduleTask(task);
                logger.info("✅ Successfully scheduled task: {}", task.getTaskName());
            } catch (Exception e) {
                logger.error("❌ Failed to schedule task: {} - {}", task.getTaskName(), e.getMessage());
                task.setStatus(TaskDefinition.TaskStatus.ERROR);
                taskRepository.save(task);
            }
        }
    }
    
    public void scheduleTask(TaskDefinition taskDef) {
        validateCronExpression(taskDef.getCronExpression());
        
        Runnable taskRunnable = () -> executeTask(taskDef);
        CronTrigger cronTrigger = new CronTrigger(taskDef.getCronExpression());
        
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(taskRunnable, cronTrigger);
        scheduledTasks.put(taskDef.getId(), scheduledFuture);
        
        // Set next execution time to current time + 1 minute as fallback
        taskDef.setNextExecution(LocalDateTime.now().plusMinutes(1));
        taskRepository.save(taskDef);
        
        logger.info("📅 Task '{}' scheduled with cron: {}", taskDef.getTaskName(), taskDef.getCronExpression());
    }
    
    public void cancelTask(Long taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            logger.info("🚫 Cancelled task with ID: {}", taskId);
        }
    }
    
    public void rescheduleTask(TaskDefinition taskDef) {
        cancelTask(taskDef.getId());
        if (taskDef.getStatus() == TaskDefinition.TaskStatus.ACTIVE) {
            scheduleTask(taskDef);
        }
    }
    
    private void executeTask(TaskDefinition taskDef) {
        try {
            logger.info("🔄 Executing task: {} [{}]", taskDef.getTaskName(), taskDef.getTaskType());
            
            // Simulate different task types
            switch (taskDef.getTaskType()) {
                case LOG_MESSAGE:
                    logger.info("📝 LOG TASK: {}", taskDef.getDescription());
                    break;
                case EMAIL_NOTIFICATION:
                    logger.info("📧 EMAIL TASK: Sending notification - {}", taskDef.getDescription());
                    break;
                case DATA_CLEANUP:
                    logger.info("🧹 CLEANUP TASK: Running data cleanup - {}", taskDef.getDescription());
                    break;
                case REPORT_GENERATION:
                    logger.info("📊 REPORT TASK: Generating report - {}", taskDef.getDescription());
                    break;
                case SYSTEM_HEALTH_CHECK:
                    logger.info("🏥 HEALTH TASK: Running system health check - {}", taskDef.getDescription());
                    break;
            }
            
            // Update last execution time
            taskDef.setLastExecuted(LocalDateTime.now());
            
            // Set next execution time to current time + 1 minute as fallback
            taskDef.setNextExecution(LocalDateTime.now().plusMinutes(1));
            
            taskRepository.save(taskDef);
            logger.info("✅ Task '{}' executed successfully", taskDef.getTaskName());
            
        } catch (Exception e) {
            logger.error("❌ Task '{}' execution failed: {}", taskDef.getTaskName(), e.getMessage());
            taskDef.setStatus(TaskDefinition.TaskStatus.ERROR);
            taskRepository.save(taskDef);
        }
    }
    
    private void validateCronExpression(String cronExpression) {
        try {
            new CronTrigger(cronExpression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
        }
    }
    
    public Map<Long, ScheduledFuture<?>> getScheduledTasks() {
        return scheduledTasks;
    }
    
    public boolean isTaskScheduled(Long taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        return future != null && !future.isCancelled() && !future.isDone();
    }
}
