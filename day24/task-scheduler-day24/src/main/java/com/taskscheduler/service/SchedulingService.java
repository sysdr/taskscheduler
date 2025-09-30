package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SchedulingService {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);
    private final TaskService taskService;
    private final TaskExecutionService taskExecutionService;
    private final ExecutorService executorService;
    
    @Autowired
    public SchedulingService(TaskService taskService, TaskExecutionService taskExecutionService) {
        this.taskService = taskService;
        this.taskExecutionService = taskExecutionService;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }
    
    @Scheduled(fixedDelay = 5000)
    public void processPendingTasks() {
        logger.debug("Processing pending tasks...");
        List<Task> readyTasks = taskService.getTasksReadyForExecution();
        
        if (!readyTasks.isEmpty()) {
            logger.info("Found {} tasks ready for execution", readyTasks.size());
            
            for (Task task : readyTasks) {
                CompletableFuture.runAsync(() -> {
                    try {
                        TaskExecutionResult result = taskExecutionService.executeTask(task);
                        if (result.isSuccess()) {
                            logger.debug("Task completed: {}", task.getName());
                        } else {
                            logger.debug("Task failed: {}", task.getName());
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected error executing task: {}", task.getName(), e);
                    }
                }, executorService);
            }
        }
    }
    
    @Scheduled(fixedDelay = 10000)
    public void processRetriableTasks() {
        logger.debug("Processing retriable tasks...");
        List<Task> retriableTasks = taskService.getRetriableTasks();
        
        if (!retriableTasks.isEmpty()) {
            logger.info("Found {} tasks ready for retry", retriableTasks.size());
            
            for (Task task : retriableTasks) {
                CompletableFuture.runAsync(() -> {
                    try {
                        TaskExecutionResult result = taskExecutionService.executeTask(task);
                        if (result.isSuccess()) {
                            logger.info("Task retry successful: {}", task.getName());
                        } else {
                            logger.info("Task retry failed: {}", task.getName());
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected error retrying task: {}", task.getName(), e);
                    }
                }, executorService);
            }
        }
    }
    
    @Scheduled(fixedDelay = 30000)
    public void logTaskStatistics() {
        long pending = taskService.getTaskCountByStatus(com.taskscheduler.model.TaskStatus.PENDING);
        long running = taskService.getTaskCountByStatus(com.taskscheduler.model.TaskStatus.RUNNING);
        long completed = taskService.getTaskCountByStatus(com.taskscheduler.model.TaskStatus.COMPLETED);
        long failed = taskService.getTaskCountByStatus(com.taskscheduler.model.TaskStatus.FAILED);
        long retrying = taskService.getTaskCountByStatus(com.taskscheduler.model.TaskStatus.RETRYING);
        
        logger.info("Task Statistics - Pending: {}, Running: {}, Completed: {}, Failed: {}, Retrying: {}", 
                   pending, running, completed, failed, retrying);
    }
}
