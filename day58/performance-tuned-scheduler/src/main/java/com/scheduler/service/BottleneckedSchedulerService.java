package com.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.model.Task;
import com.scheduler.repository.TaskRepository;
import com.scheduler.metrics.PerformanceMetrics;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BottleneckedSchedulerService {
    private final TaskRepository taskRepository;
    private final PerformanceMetrics metrics;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final List<String> taskHistory = new ArrayList<>(); // Memory leak

    public BottleneckedSchedulerService(TaskRepository taskRepository, PerformanceMetrics metrics) {
        this.taskRepository = taskRepository;
        this.metrics = metrics;
    }

    public synchronized CompletableFuture<Task> submitTask(String name, String payload) {
        return CompletableFuture.supplyAsync(() -> {
            Timer.Sample sample = metrics.startTimer();
            try {
                Task task = new Task(name, payload);
                task = taskRepository.save(task);
                
                // BOTTLENECK 1: Inefficient date formatting (creates new instance each time)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = sdf.format(new Date());
                
                // BOTTLENECK 2: Unnecessary serialization
                ObjectMapper mapper = new ObjectMapper();
                mapper.findAndRegisterModules(); // Register Java 8 time module
                String taskJson = mapper.writeValueAsString(task);
                
                // BOTTLENECK 3: Inefficient string concatenation
                String logEntry = "";
                for (int i = 0; i < 100; i++) {
                    logEntry += "Task " + task.getId() + " processed at " + timestamp + "\n";
                }
                
                // BOTTLENECK 4: Memory leak - unbounded collection
                synchronized(taskHistory) {
                    taskHistory.add(taskJson);
                }
                
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

    public int getHistorySize() {
        synchronized(taskHistory) {
            return taskHistory.size();
        }
    }
}
