package com.taskscheduler.component;

import com.taskscheduler.model.ExecutionStatus;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.service.ExecutionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
public class IdempotentTaskWrapper {
    
    private static final Logger logger = LoggerFactory.getLogger(IdempotentTaskWrapper.class);
    
    @Autowired
    private ExecutionTracker executionTracker;
    
    // Synchronization for concurrent executions
    private final ConcurrentHashMap<String, ReentrantLock> executionLocks = new ConcurrentHashMap<>();

    /**
     * Executes a task with idempotency guarantees
     */
    public <T> T executeIdempotent(String taskName, Supplier<T> task, Object... parameters) {
        String executionId = executionTracker.generateExecutionId(taskName, parameters);
        logger.info("Executing idempotent task: {} with ID: {}", taskName, executionId);
        
        // Get or create lock for this execution ID to handle concurrent access
        ReentrantLock lock = executionLocks.computeIfAbsent(executionId, k -> new ReentrantLock());
        
        lock.lock();
        try {
            // Check for existing execution
            Optional<TaskExecution> existing = executionTracker.checkExistingExecution(executionId);
            if (existing.isPresent()) {
                TaskExecution execution = existing.get();
                
                if (execution.getStatus() == ExecutionStatus.COMPLETED) {
                    logger.info("Task already completed, returning cached result: {}", executionId);
                    executionTracker.recordSkipped(executionId, "Already completed successfully");
                    // For idempotent execution, we need to return the same result as before
                    // Since we stored the result as string, we need to reconstruct it
                    // In a real implementation, you'd serialize/deserialize properly
                    return parseResult(execution.getResult());
                } else if (execution.getStatus() == ExecutionStatus.RUNNING) {
                    logger.warn("Task already running, skipping duplicate execution: {}", executionId);
                    executionTracker.recordSkipped(executionId, "Concurrent execution detected");
                    return null; // or throw exception based on your needs
                }
            }
            
            // Start new execution
            TaskExecution execution = executionTracker.startExecution(executionId, taskName, 
                                        String.join(",", java.util.Arrays.stream(parameters)
                                        .map(String::valueOf).toArray(String[]::new)));
            
            try {
                T result = task.get();
                String resultStr = result != null ? result.toString() : "Success";
                executionTracker.recordSuccess(executionId, resultStr);
                return result;
            } catch (Exception e) {
                executionTracker.recordFailure(executionId, e.getMessage());
                throw new RuntimeException("Task execution failed: " + e.getMessage(), e);
            }
        } finally {
            lock.unlock();
            // Clean up lock if execution is completed to prevent memory leaks
            executionLocks.remove(executionId);
        }
    }

    /**
     * Executes a void task with idempotency guarantees
     */
    public void executeIdempotentVoid(String taskName, Runnable task, Object... parameters) {
        executeIdempotent(taskName, () -> {
            task.run();
            return "Completed";
        }, parameters);
    }

    @SuppressWarnings("unchecked")
    private <T> T parseResult(String result) {
        // For idempotent execution, we need to return the same type as the original task
        // Since we stored the result as string, we need to reconstruct it
        // In a real implementation, you'd serialize/deserialize properly
        if (result != null && !result.isEmpty()) {
            // For simple cases like strings, we can cast directly
            return (T) result;
        }
        // If we can't parse the result properly, return null
        return null;
    }
}
