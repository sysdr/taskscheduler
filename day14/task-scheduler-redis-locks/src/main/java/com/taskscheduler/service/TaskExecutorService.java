package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskExecutorService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private RedisLockService lockService;
    
    private final String executorId = "executor-" + UUID.randomUUID().toString().substring(0, 8);
    
    @Scheduled(fixedDelay = 2000) // Poll every 2 seconds
    public void pollAndExecuteTasks() {
        List<Task> pendingTasks = taskRepository.findPendingTasks();
        
        for (Task task : pendingTasks) {
            if (tryExecuteTask(task)) {
                break; // Execute one task at a time per executor
            }
        }
    }
    
    private boolean tryExecuteTask(Task task) {
        String taskId = task.getId().toString();
        
        // Try to acquire lock
        if (lockService.acquireLock(taskId, executorId)) {
            System.out.println("🔒 Executor " + executorId + " acquired lock for task " + taskId);
            
            // Update task status to RUNNING
            task.setStatus("RUNNING");
            task.setStartedAt(LocalDateTime.now());
            task.setExecutorId(executorId);
            taskRepository.save(task);
            
            // Execute task asynchronously
            CompletableFuture.runAsync(() -> executeTaskAsync(task));
            return true;
        }
        return false;
    }
    
    private void executeTaskAsync(Task task) {
        try {
            String taskId = task.getId().toString();
            
            // Simulate task execution
            System.out.println("🚀 Executor " + executorId + " executing task: " + task.getName());
            Thread.sleep(task.getExecutionTimeMs());
            
            // Update task completion
            task.setStatus("COMPLETED");
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            System.out.println("✅ Executor " + executorId + " completed task: " + task.getName());
            
        } catch (Exception e) {
            System.err.println("❌ Task execution failed: " + e.getMessage());
            task.setStatus("FAILED");
            taskRepository.save(task);
        } finally {
            // Always release the lock
            lockService.releaseLock(task.getId().toString(), executorId);
            System.out.println("🔓 Executor " + executorId + " released lock for task " + task.getId());
        }
    }
    
    public String getExecutorId() {
        return executorId;
    }
}
