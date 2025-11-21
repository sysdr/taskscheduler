package com.scheduler.service;

import com.scheduler.metrics.TaskMetrics;
import com.scheduler.model.Task;
import com.scheduler.model.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMetrics taskMetrics;
    private final Random random = new Random();

    public Task submitTask(String name, String type) {
        Task task = new Task();
        task.setName(name);
        task.setType(type);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        
        task = taskRepository.save(task);
        taskMetrics.recordTaskSubmitted(type);
        
        log.info("Task submitted: {} [{}]", name, type);
        executeTaskAsync(task.getId());
        
        return task;
    }

    @Async
    public CompletableFuture<Task> executeTaskAsync(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        
        task.setStatus(Task.TaskStatus.RUNNING);
        task.setStartedAt(LocalDateTime.now());
        taskRepository.save(task);
        taskMetrics.recordTaskStarted();
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Simulate task execution with varying duration
            int executionTimeMs = 100 + random.nextInt(2000);
            Thread.sleep(executionTimeMs);
            
            // Simulate occasional failures (10% chance)
            if (random.nextInt(100) < 10) {
                throw new RuntimeException("Task execution failed randomly");
            }
            
            long duration = System.currentTimeMillis() - startTime;
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            task.setDurationMs(duration);
            taskRepository.save(task);
            
            taskMetrics.recordTaskCompleted(duration, task.getType());
            log.info("Task completed: {} in {}ms", task.getName(), duration);
            
        } catch (Exception e) {
            task.setStatus(Task.TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            taskMetrics.recordTaskFailed(task.getType());
            log.error("Task failed: {}", task.getName(), e);
        }
        
        return CompletableFuture.completedFuture(task);
    }

    // Auto-generate tasks for demo purposes
    @Scheduled(fixedDelay = 5000)
    public void autoGenerateTasks() {
        String[] types = {"EMAIL", "REPORT", "DATA_SYNC", "BACKUP"};
        String type = types[random.nextInt(types.length)];
        submitTask("Auto-" + System.currentTimeMillis(), type);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}
