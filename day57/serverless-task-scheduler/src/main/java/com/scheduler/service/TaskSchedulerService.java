package com.scheduler.service;

import com.scheduler.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {
    
    private final TaskRepository taskRepository;
    private final LambdaExecutorService lambdaExecutorService;
    private final ExecutorService localExecutor = Executors.newVirtualThreadPerTaskExecutor();
    
    @Scheduled(fixedDelay = 2000)
    public void processPendingTasks() {
        List<Task> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        
        for (Task task : pendingTasks) {
            if (task.getExecutionMode() == ExecutionMode.LAMBDA || 
                (task.getExecutionMode() == ExecutionMode.AUTO && shouldUseLambda(task))) {
                
                task.setStatus(TaskStatus.QUEUED);
                taskRepository.save(task);
                
                lambdaExecutorService.executeTaskAsync(task);
            } else {
                executeLocally(task);
            }
        }
    }
    
    @Scheduled(fixedDelay = 5000)
    public void processRetryTasks() {
        List<Task> retryTasks = taskRepository.findByStatus(TaskStatus.RETRYING);
        
        for (Task task : retryTasks) {
            // Exponential backoff
            long backoffMs = (long) Math.pow(2, task.getRetryCount()) * 1000;
            
            try {
                Thread.sleep(backoffMs);
                
                task.setStatus(TaskStatus.PENDING);
                taskRepository.save(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    public void warmLambdaPool() {
        lambdaExecutorService.warmLambdaFunctions();
    }
    
    private boolean shouldUseLambda(Task task) {
        // Decision logic: use Lambda if local pool is busy
        // In real implementation, check actual thread pool metrics
        return Math.random() > 0.5; // Simplified
    }
    
    private void executeLocally(Task task) {
        task.setStatus(TaskStatus.RUNNING);
        taskRepository.save(task);
        
        localExecutor.submit(() -> {
            try {
                log.info("Executing task {} locally", task.getId());
                
                // Simulate task execution
                Thread.sleep((long) (Math.random() * 3000 + 1000));
                
                task.setStatus(TaskStatus.COMPLETED);
                task.setResult("Local execution completed");
            } catch (Exception e) {
                task.setStatus(TaskStatus.FAILED);
                task.setErrorMessage(e.getMessage());
            } finally {
                taskRepository.save(task);
            }
        });
    }
}
