package com.taskscheduler.component;

import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TaskExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorService.class);
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskSchedulerLifecycleManager lifecycleManager;
    
    @Value("${task.scheduler.core-pool-size:5}")
    private int corePoolSize;
    
    private final AtomicBoolean acceptingNewTasks = new AtomicBoolean(true);
    private ScheduledExecutorService executorService;
    
    @jakarta.annotation.PostConstruct
    public void initialize() {
        this.executorService = Executors.newScheduledThreadPool(corePoolSize);
        logger.info("🚀 Task Executor Service initialized with {} threads", corePoolSize);
    }
    
    @Async
    public void executeTask(Long taskId) {
        if (!acceptingNewTasks.get()) {
            logger.warn("⛔ Rejecting new task {} - shutdown in progress", taskId);
            return;
        }
        
        try {
            Task task = taskService.getTaskById(taskId).orElse(null);
            if (task == null) {
                logger.error("❌ Task {} not found", taskId);
                return;
            }
            
            logger.info("🎯 Starting execution of task: {} ({})", task.getName(), taskId);
            taskService.updateTaskStatus(taskId, Task.TaskStatus.RUNNING);
            
            // Simulate task execution with progress updates
            executeTaskWithProgress(task);
            
        } catch (InterruptedException e) {
            logger.warn("⚠️ Task {} interrupted during execution", taskId);
            taskService.suspendTask(taskId, "Task interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("❌ Task {} failed with error: {}", taskId, e.getMessage());
            taskService.updateTaskStatus(taskId, Task.TaskStatus.FAILED);
        } finally {
            // Notify lifecycle manager if shutdown is in progress
            if (lifecycleManager.isShutdownInitiated()) {
                lifecycleManager.notifyTaskCompleted();
            }
        }
    }
    
    private void executeTaskWithProgress(Task task) throws InterruptedException {
        int totalDuration = task.getDurationSeconds();
        int progressSteps = Math.min(10, totalDuration); // Update progress 10 times or once per second
        int stepDuration = totalDuration / progressSteps;
        
        for (int step = 1; step <= progressSteps; step++) {
            // Check if shutdown is initiated
            if (lifecycleManager.isShutdownInitiated()) {
                logger.info("🛑 Task {} suspended due to shutdown", task.getId());
                taskService.suspendTask(task.getId(), "Graceful shutdown");
                return;
            }
            
            // Simulate work
            Thread.sleep(stepDuration * 1000L);
            
            // Update progress
            int progress = (step * 100) / progressSteps;
            taskService.updateTaskProgress(task.getId(), progress);
            logger.debug("📊 Task {} progress: {}%", task.getId(), progress);
        }
        
        // Task completed successfully
        taskService.updateTaskStatus(task.getId(), Task.TaskStatus.COMPLETED);
        logger.info("✅ Task {} completed successfully", task.getId());
    }
    
    public void stopAcceptingNewTasks() {
        acceptingNewTasks.set(false);
        logger.info("🚫 Stopped accepting new tasks");
    }
    
    public void forceShutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            logger.info("💀 Force shutdown of executor service completed");
        }
    }
    
    public boolean isAcceptingNewTasks() {
        return acceptingNewTasks.get();
    }
}
