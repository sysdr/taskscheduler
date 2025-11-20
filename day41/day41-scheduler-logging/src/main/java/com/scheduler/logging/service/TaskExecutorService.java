package com.scheduler.logging.service;

import com.scheduler.logging.model.Task;
import com.scheduler.logging.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TaskExecutorService {
    private static final Logger log = LoggerFactory.getLogger(TaskExecutorService.class);
    
    private final TaskRepository taskRepository;
    private final TaskLoggingService loggingService;
    private final Random random = new Random();
    
    public TaskExecutorService(TaskRepository taskRepository, TaskLoggingService loggingService) {
        this.taskRepository = taskRepository;
        this.loggingService = loggingService;
    }
    
    public Task createTask(String taskType, String payload) {
        Task task = new Task();
        task.setTaskType(taskType);
        task.setPayload(payload);
        task.setCorrelationId(UUID.randomUUID().toString());
        task = taskRepository.save(task);
        
        loggingService.logTaskCreated(task);
        return task;
    }
    
    @Scheduled(fixedRate = 5000)
    public void processPendingTasks() {
        List<Task> pendingTasks = taskRepository.findByStatusOrderByCreatedAtAsc(Task.TaskStatus.PENDING);
        
        for (Task task : pendingTasks) {
            executeTask(task);
        }
    }
    
    @Async
    public void executeTask(Task task) {
        long startTime = System.currentTimeMillis();
        
        try {
            task.setStatus(Task.TaskStatus.RUNNING);
            task.setStartedAt(Instant.now());
            taskRepository.save(task);
            
            loggingService.logTaskStarted(task);
            
            // Simulate task execution with logging
            loggingService.logTaskProgress(task, "Processing payload: {}", task.getPayload());
            
            simulateTaskExecution(task);
            
            loggingService.logTaskProgress(task, "Validation complete, finalizing task");
            
            // Simulate random failures for demonstration
            if (random.nextInt(100) < 15) {
                throw new RuntimeException("Simulated transient failure");
            }
            
            long duration = System.currentTimeMillis() - startTime;
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompletedAt(Instant.now());
            task.setExecutionTimeMs(duration);
            taskRepository.save(task);
            
            loggingService.logTaskCompleted(task, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            if (task.getRetryCount() < 3) {
                task.setStatus(Task.TaskStatus.RETRYING);
                task.setRetryCount(task.getRetryCount() + 1);
                loggingService.logTaskRetrying(task, task.getRetryCount(), e.getMessage());
                
                // Reset to pending for retry
                task.setStatus(Task.TaskStatus.PENDING);
            } else {
                task.setStatus(Task.TaskStatus.FAILED);
                task.setErrorMessage(e.getMessage());
                loggingService.logTaskFailed(task, e, duration);
            }
            
            task.setExecutionTimeMs(duration);
            taskRepository.save(task);
        }
    }
    
    private void simulateTaskExecution(Task task) throws InterruptedException {
        int baseTime = switch (task.getTaskType()) {
            case "EMAIL_NOTIFICATION" -> 100;
            case "PAYMENT_PROCESSING" -> 500;
            case "DATA_SYNC" -> 300;
            case "REPORT_GENERATION" -> 800;
            default -> 200;
        };
        
        int actualTime = baseTime + random.nextInt(200);
        Thread.sleep(actualTime);
        
        loggingService.logTaskProgress(task, "Task {} processing took {}ms", task.getTaskType(), actualTime);
    }
    
    // Generate sample tasks for demonstration
    @Scheduled(fixedRate = 10000)
    public void generateSampleTasks() {
        String[] taskTypes = {"EMAIL_NOTIFICATION", "PAYMENT_PROCESSING", "DATA_SYNC", "REPORT_GENERATION"};
        String[] payloads = {
            "{\"recipient\":\"user@example.com\",\"template\":\"welcome\"}",
            "{\"amount\":99.99,\"currency\":\"USD\",\"merchantId\":\"m_123\"}",
            "{\"source\":\"crm\",\"target\":\"analytics\",\"records\":150}",
            "{\"reportType\":\"daily\",\"format\":\"pdf\"}"
        };
        
        int idx = random.nextInt(taskTypes.length);
        createTask(taskTypes[idx], payloads[idx]);
    }
}
