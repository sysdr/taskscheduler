package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CircuitBreakerTaskService circuitBreakerService;
    
    public Task createTask(String name, String type) {
        Task task = new Task(name, type);
        return taskRepository.save(task);
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<Task> getRecentTasks() {
        return taskRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    @Scheduled(fixedDelay = 5000) // Process every 5 seconds
    public void processPendingTasks() {
        List<Task> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        
        for (Task task : pendingTasks) {
            processTaskAsync(task);
        }
    }
    
    public void processTaskAsync(Task task) {
        CompletableFuture.runAsync(() -> {
            try {
                task.setStatus(TaskStatus.EXECUTING);
                task.setExecutedAt(LocalDateTime.now());
                taskRepository.save(task);
                
                processTaskWithCircuitBreaker(task);
                
                task.setStatus(TaskStatus.SUCCESS);
                taskRepository.save(task);
                
            } catch (Exception e) {
                task.setStatus(TaskStatus.FAILED);
                task.setErrorMessage(e.getMessage());
                task.setRetryCount(task.getRetryCount() + 1);
                taskRepository.save(task);
            }
        });
    }
    
    private void processTaskWithCircuitBreaker(Task task) {
        switch (task.getType().toUpperCase()) {
            case "PAYMENT":
                String paymentResult = circuitBreakerService.processPaymentWithCircuitBreaker(task, 100.0);
                System.out.println("Payment processed: " + paymentResult);
                break;
                
            case "NOTIFICATION":
                circuitBreakerService.sendNotificationWithCircuitBreaker(task, "Task completed: " + task.getName());
                break;
                
            case "AUDIT":
                circuitBreakerService.recordAuditWithCircuitBreaker(task, "TASK_EXECUTED");
                break;
                
            default:
                // Simulate general processing
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Task processing interrupted", e);
                }
                System.out.println("Processed task: " + task.getName());
        }
    }
}
