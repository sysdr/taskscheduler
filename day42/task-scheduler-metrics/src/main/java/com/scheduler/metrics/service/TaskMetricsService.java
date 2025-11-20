package com.scheduler.metrics.service;

import com.scheduler.metrics.model.Task;
import com.scheduler.metrics.model.TaskStatus;
import com.scheduler.metrics.repository.TaskRepository;
import io.micrometer.core.instrument.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskMetricsService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskMetricsService.class);
    
    private final MeterRegistry meterRegistry;
    private final TaskRepository taskRepository;
    
    // Counters
    private final Counter taskSubmittedCounter;
    private final Counter taskCompletedCounter;
    private final Counter taskFailedCounter;
    
    // Gauges
    private final AtomicInteger activeTasksGauge;
    private final AtomicInteger queuedTasksGauge;
    
    // Timers by type
    private final ConcurrentHashMap<String, Timer> taskTimersByType;
    
    public TaskMetricsService(MeterRegistry meterRegistry, TaskRepository taskRepository) {
        this.meterRegistry = meterRegistry;
        this.taskRepository = taskRepository;
        this.taskTimersByType = new ConcurrentHashMap<>();
        
        // Initialize counters
        this.taskSubmittedCounter = Counter.builder("task.submitted.total")
                .description("Total number of tasks submitted")
                .register(meterRegistry);
        
        this.taskCompletedCounter = Counter.builder("task.completed.total")
                .description("Total number of tasks completed successfully")
                .register(meterRegistry);
        
        this.taskFailedCounter = Counter.builder("task.failed.total")
                .description("Total number of tasks that failed")
                .register(meterRegistry);
        
        // Initialize gauges
        this.activeTasksGauge = new AtomicInteger(0);
        Gauge.builder("task.active.count", activeTasksGauge, AtomicInteger::get)
                .description("Current number of actively executing tasks")
                .register(meterRegistry);
        
        this.queuedTasksGauge = new AtomicInteger(0);
        Gauge.builder("task.queued.count", queuedTasksGauge, AtomicInteger::get)
                .description("Current number of queued tasks")
                .register(meterRegistry);
        
        // Register database-backed gauges
        Gauge.builder("task.total.count", taskRepository, TaskRepository::count)
                .description("Total number of tasks in database")
                .register(meterRegistry);
        
        logger.info("Task metrics service initialized");
    }
    
    public void recordTaskSubmitted(Task task) {
        taskSubmittedCounter.increment();
        queuedTasksGauge.incrementAndGet();
        
        // Increment counter with type tag
        meterRegistry.counter("task.submitted", 
                "type", task.getType(),
                "priority", task.getPriority())
                .increment();
        
        logger.debug("Task submitted: {} [type={}, priority={}]", 
                task.getName(), task.getType(), task.getPriority());
    }
    
    public void recordTaskStarted(Task task) {
        queuedTasksGauge.decrementAndGet();
        activeTasksGauge.incrementAndGet();
        
        logger.debug("Task started: {}", task.getName());
    }
    
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordTaskCompleted(Task task, Timer.Sample timerSample) {
        activeTasksGauge.decrementAndGet();
        taskCompletedCounter.increment();
        
        // Get or create timer for this task type
        Timer timer = taskTimersByType.computeIfAbsent(task.getType(), type ->
                Timer.builder("task.execution")
                        .description("Task execution time")
                        .tag("type", type)
                        .tag("status", "success")
                        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );
        
        timerSample.stop(timer);
        
        // Counter with tags
        meterRegistry.counter("task.completed",
                "type", task.getType(),
                "priority", task.getPriority())
                .increment();
        
        logger.debug("Task completed: {} [{}ms]", 
                task.getName(), task.getExecutionTimeMs());
    }
    
    public void recordTaskFailed(Task task, Timer.Sample timerSample, String errorMessage) {
        activeTasksGauge.decrementAndGet();
        taskFailedCounter.increment();
        
        // Record timing even for failures
        Timer failureTimer = Timer.builder("task.execution")
                .description("Task execution time")
                .tag("type", task.getType())
                .tag("status", "failure")
                .register(meterRegistry);
        
        timerSample.stop(failureTimer);
        
        // Counter with tags
        meterRegistry.counter("task.failed",
                "type", task.getType(),
                "priority", task.getPriority(),
                "error", categorizeError(errorMessage))
                .increment();
        
        logger.warn("Task failed: {} - {}", task.getName(), errorMessage);
    }
    
    public void recordRetry(Task task) {
        meterRegistry.counter("task.retry",
                "type", task.getType(),
                "attempt", String.valueOf(task.getRetryCount()))
                .increment();
        
        logger.debug("Task retry: {} [attempt={}]", 
                task.getName(), task.getRetryCount());
    }
    
    private String categorizeError(String errorMessage) {
        if (errorMessage == null) return "unknown";
        if (errorMessage.contains("timeout")) return "timeout";
        if (errorMessage.contains("connection")) return "connection";
        return "other";
    }
    
    public int getActiveTaskCount() {
        return activeTasksGauge.get();
    }
    
    public int getQueuedTaskCount() {
        return queuedTasksGauge.get();
    }
}
