package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.repository.TaskRepository;
import com.scheduler.metrics.PerformanceMetrics;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OptimizedSchedulerService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final TaskRepository taskRepository;
    private final PerformanceMetrics metrics;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public OptimizedSchedulerService(TaskRepository taskRepository, PerformanceMetrics metrics) {
        this.taskRepository = taskRepository;
        this.metrics = metrics;
    }

    public CompletableFuture<Task> submitTask(String name, String payload) {
        return CompletableFuture.supplyAsync(() -> {
            Timer.Sample sample = metrics.startTimer();
            try {
                Task task = new Task(name, payload);
                task = taskRepository.save(task);
                
                // OPTIMIZATION 1: Reuse DateTimeFormatter (thread-safe)
                String timestamp = LocalDateTime.now().format(FORMATTER);
                
                // OPTIMIZATION 2: Skip unnecessary serialization
                // Only serialize if actually needed
                
                // OPTIMIZATION 3: Use StringBuilder
                StringBuilder logEntry = new StringBuilder(100);
                logEntry.append("Task ").append(task.getId())
                       .append(" processed at ").append(timestamp);
                
                // OPTIMIZATION 4: No unbounded collection
                // Use circular buffer or external logging if needed
                
                // Simulate work
                Thread.sleep(50 + (long)(Math.random() * 50));
                
                task.setStatus("COMPLETED");
                task.setExecutedAt(LocalDateTime.now());
                task.setExecutionTimeMs(System.currentTimeMillis() - 
                    task.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                
                taskRepository.save(task);
                metrics.recordTaskProcessed();
                return task;
            } catch (Exception e) {
                metrics.recordTaskFailed();
                throw new RuntimeException(e);
            } finally {
                metrics.recordExecutionTime(sample);
            }
        }, executor);
    }
}
