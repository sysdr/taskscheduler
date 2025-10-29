package com.taskscheduler.component;

import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class TaskSchedulerLifecycleManager implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerLifecycleManager.class);
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskExecutorService taskExecutorService;
    
    @Value("${task.scheduler.graceful-shutdown-timeout:30}")
    private int shutdownTimeoutSeconds;
    
    private volatile boolean shutdownInitiated = false;
    private CountDownLatch shutdownLatch = new CountDownLatch(0);
    
    @PreDestroy
    @Override
    public void destroy() throws Exception {
        logger.info("🛑 Initiating graceful shutdown of Task Scheduler...");
        initiateGracefulShutdown();
    }
    
    public void initiateGracefulShutdown() {
        if (shutdownInitiated) {
            logger.warn("Shutdown already initiated, ignoring duplicate request");
            return;
        }
        
        shutdownInitiated = true;
        
        try {
            // Step 1: Stop accepting new tasks
            logger.info("📢 Stopping acceptance of new tasks...");
            taskExecutorService.stopAcceptingNewTasks();
            
            // Step 2: Get list of active/pending tasks
            List<Task> activeTasks = taskService.getActiveOrPendingTasks();
            logger.info("🔍 Found {} active/pending tasks to handle", activeTasks.size());
            
            if (activeTasks.isEmpty()) {
                logger.info("✅ No active tasks found, proceeding with immediate shutdown");
                return;
            }
            
            // Step 3: Set up countdown latch for active tasks
            shutdownLatch = new CountDownLatch(activeTasks.size());
            
            // Step 4: Wait for tasks to complete or timeout
            logger.info("⏳ Waiting up to {} seconds for {} tasks to complete...", 
                       shutdownTimeoutSeconds, activeTasks.size());
            
            boolean allTasksCompleted = shutdownLatch.await(shutdownTimeoutSeconds, TimeUnit.SECONDS);
            
            if (allTasksCompleted) {
                logger.info("✅ All tasks completed successfully during graceful shutdown");
            } else {
                logger.warn("⚠️ Shutdown timeout reached, suspending remaining tasks...");
                suspendRemainingTasks();
            }
            
        } catch (InterruptedException e) {
            logger.error("❌ Graceful shutdown interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            // Step 5: Final cleanup
            performFinalCleanup();
            logger.info("🏁 Graceful shutdown completed");
        }
    }
    
    private void suspendRemainingTasks() {
        List<Task> stillActiveTasks = taskService.getActiveOrPendingTasks();
        logger.info("💤 Suspending {} remaining active tasks", stillActiveTasks.size());
        
        for (Task task : stillActiveTasks) {
            if (task.getStatus() == Task.TaskStatus.RUNNING || 
                task.getStatus() == Task.TaskStatus.PENDING) {
                taskService.suspendTask(task.getId(), "Application shutdown");
                logger.info("💤 Suspended task: {} ({})", task.getName(), task.getId());
            }
        }
    }
    
    private void performFinalCleanup() {
        logger.info("🧹 Performing final cleanup...");
        
        // Clean up executor service
        taskExecutorService.forceShutdown();
        
        // Log final statistics
        logFinalStatistics();
    }
    
    private void logFinalStatistics() {
        logger.info("📊 Final Task Statistics:");
        logger.info("   - Completed: {}", taskService.getAllTasks().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count());
        logger.info("   - Suspended: {}", taskService.getAllTasks().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.SUSPENDED).count());
        logger.info("   - Failed: {}", taskService.getAllTasks().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.FAILED).count());
    }
    
    public void notifyTaskCompleted() {
        if (shutdownInitiated) {
            shutdownLatch.countDown();
            logger.debug("📈 Task completed during shutdown. Remaining: {}", shutdownLatch.getCount());
        }
    }
    
    public boolean isShutdownInitiated() {
        return shutdownInitiated;
    }
}
