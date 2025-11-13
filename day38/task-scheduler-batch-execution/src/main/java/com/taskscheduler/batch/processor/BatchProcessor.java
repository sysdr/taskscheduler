package com.taskscheduler.batch.processor;

import com.taskscheduler.batch.model.BatchMetrics;
import com.taskscheduler.batch.model.Task;
import com.taskscheduler.batch.repository.BatchMetricsRepository;
import com.taskscheduler.batch.repository.TaskRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class BatchProcessor {
    
    private final TaskRepository taskRepository;
    private final BatchMetricsRepository metricsRepository;
    private final Counter batchProcessedCounter;
    private final Counter tasksProcessedCounter;
    private final Timer batchProcessingTimer;
    
    @Value("${batch.processor.max-retries:3}")
    private int maxRetries;
    
    public BatchProcessor(TaskRepository taskRepository,
                         BatchMetricsRepository metricsRepository,
                         MeterRegistry meterRegistry) {
        this.taskRepository = taskRepository;
        this.metricsRepository = metricsRepository;
        this.batchProcessedCounter = Counter.builder("batch.processed.total")
                .description("Total number of batches processed")
                .register(meterRegistry);
        this.tasksProcessedCounter = Counter.builder("tasks.processed.total")
                .description("Total number of tasks processed")
                .register(meterRegistry);
        this.batchProcessingTimer = Timer.builder("batch.processing.time")
                .description("Time taken to process a batch")
                .register(meterRegistry);
    }
    
    @Transactional
    public BatchResult processBatch(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return BatchResult.empty();
        }
        
        String batchId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        long startNanos = System.nanoTime();
        
        log.info("Processing batch {} with {} tasks", batchId, tasks.size());
        
        List<Task> successTasks = new ArrayList<>();
        List<Task> failedTasks = new ArrayList<>();
        
        // Process each task in the batch
        for (Task task : tasks) {
            try {
                task.setBatchId(batchId);
                task.setStatus(Task.TaskStatus.PROCESSING);
                
                long taskStartNanos = System.nanoTime();
                
                // Simulate task execution (in real-world: call external API, process data, etc.)
                executeTask(task);
                
                long taskDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - taskStartNanos);
                
                task.setStatus(Task.TaskStatus.COMPLETED);
                task.setProcessedAt(LocalDateTime.now());
                task.setProcessingDurationMs(taskDurationMs);
                successTasks.add(task);
                
            } catch (Exception e) {
                log.error("Task {} failed: {}", task.getTaskId(), e.getMessage());
                task.setErrorMessage(e.getMessage());
                
                if (task.getRetryCount() < maxRetries) {
                    task.setStatus(Task.TaskStatus.RETRY);
                    task.setRetryCount(task.getRetryCount() + 1);
                } else {
                    task.setStatus(Task.TaskStatus.FAILED);
                }
                failedTasks.add(task);
            }
        }
        
        // Batch save all tasks (leveraging JPA batch operations)
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(successTasks);
        allTasks.addAll(failedTasks);
        taskRepository.saveAll(allTasks);
        
        long totalDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        double avgTaskTime = tasks.isEmpty() ? 0.0 : (double) totalDurationMs / tasks.size();
        
        // Save batch metrics
        BatchMetrics metrics = BatchMetrics.builder()
                .batchId(batchId)
                .batchSize(tasks.size())
                .successCount(successTasks.size())
                .failureCount(failedTasks.size())
                .totalProcessingTimeMs(totalDurationMs)
                .avgTaskProcessingTimeMs(avgTaskTime)
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .processorInfo(Thread.currentThread().getName())
                .build();
        
        metricsRepository.save(metrics);
        
        // Update metrics
        batchProcessedCounter.increment();
        tasksProcessedCounter.increment(tasks.size());
        batchProcessingTimer.record(totalDurationMs, TimeUnit.MILLISECONDS);
        
        log.info("Batch {} completed: {} successful, {} failed, {}ms total",
                batchId, successTasks.size(), failedTasks.size(), totalDurationMs);
        
        return new BatchResult(batchId, tasks.size(), successTasks.size(), failedTasks.size(), totalDurationMs);
    }
    
    private void executeTask(Task task) throws Exception {
        // Simulate different task types with different processing times
        int processingTime = switch (task.getTaskType()) {
            case "EMAIL" -> 50 + (int) (Math.random() * 50);
            case "SMS" -> 30 + (int) (Math.random() * 30);
            case "PUSH" -> 20 + (int) (Math.random() * 20);
            case "REPORT" -> 100 + (int) (Math.random() * 100);
            default -> 40 + (int) (Math.random() * 40);
        };
        
        Thread.sleep(processingTime);
        
        // Simulate 5% failure rate
        if (Math.random() < 0.05) {
            throw new RuntimeException("Simulated task failure");
        }
    }
    
    public record BatchResult(String batchId, int totalTasks, int successCount, int failureCount, long durationMs) {
        public static BatchResult empty() {
            return new BatchResult("", 0, 0, 0, 0);
        }
    }
}
