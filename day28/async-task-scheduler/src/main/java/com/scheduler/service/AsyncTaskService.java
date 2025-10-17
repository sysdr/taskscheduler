package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AsyncTaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Async("emailTaskExecutor")
    public CompletableFuture<String> processEmailTask(Task task) {
        return processTaskAsync(task, "Email");
    }
    
    @Async("reportTaskExecutor")
    public CompletableFuture<String> processReportTask(Task task) {
        return processTaskAsync(task, "Report");
    }
    
    @Async("dataTaskExecutor")
    public CompletableFuture<String> processDataTask(Task task) {
        return processTaskAsync(task, "Data");
    }
    
    private CompletableFuture<String> processTaskAsync(Task task, String taskTypeLabel) {
        String threadName = Thread.currentThread().getName();
        logger.info("Starting {} task '{}' on thread: {}", taskTypeLabel, task.getName(), threadName);
        
        try {
            // Update task status to executing
            task.setStatus(TaskStatus.EXECUTING);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            // Simulate processing time
            int processingTime = task.getProcessingTimeSeconds() != null ? 
                task.getProcessingTimeSeconds() : ThreadLocalRandom.current().nextInt(3, 8);
            
            Thread.sleep(processingTime * 1000);
            
            // Simulate random success/failure
            if (ThreadLocalRandom.current().nextDouble() < 0.15) {
                throw new RuntimeException("Simulated processing error");
            }
            
            // Complete successfully
            String result = String.format("%s task '%s' completed successfully in %d seconds on thread %s", 
                taskTypeLabel, task.getName(), processingTime, threadName);
            
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            task.setResult(result);
            taskRepository.save(task);
            
            logger.info("Completed {} task '{}' on thread: {}", taskTypeLabel, task.getName(), threadName);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            logger.error("Failed {} task '{}' on thread: {}", taskTypeLabel, task.getName(), threadName, e);
            
            task.setStatus(TaskStatus.FAILED);
            task.setCompletedAt(LocalDateTime.now());
            task.setErrorMessage(e.getMessage());
            taskRepository.save(task);
            
            return CompletableFuture.failedFuture(e);
        }
    }
}
