package com.scheduler.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class TaskMetrics {
    private final Counter taskSubmittedCounter;
    private final Counter taskCompletedCounter;
    private final Counter taskFailedCounter;
    private final Timer taskDurationTimer;
    private final AtomicLong activeTasksGauge;
    private final AtomicLong queueDepthGauge;
    private final MeterRegistry registry;

    public TaskMetrics(MeterRegistry registry) {
        this.registry = registry;
        
        this.taskSubmittedCounter = Counter.builder("task.submitted.total")
                .description("Total number of tasks submitted")
                .register(registry);
        
        this.taskCompletedCounter = Counter.builder("task.completed.total")
                .description("Total number of tasks completed successfully")
                .register(registry);
        
        this.taskFailedCounter = Counter.builder("task.failed.total")
                .description("Total number of tasks failed")
                .register(registry);
        
        this.taskDurationTimer = Timer.builder("task.execution.duration")
                .description("Task execution duration")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
        
        this.activeTasksGauge = new AtomicLong(0);
        Gauge.builder("task.active.count", activeTasksGauge, AtomicLong::get)
                .description("Number of currently executing tasks")
                .register(registry);
        
        this.queueDepthGauge = new AtomicLong(0);
        Gauge.builder("task.queue.depth", queueDepthGauge, AtomicLong::get)
                .description("Number of tasks waiting in queue")
                .register(registry);
    }

    public void recordTaskSubmitted(String taskType) {
        taskSubmittedCounter.increment();
        queueDepthGauge.incrementAndGet();
        Tags.of("type", taskType);
    }

    public void recordTaskStarted() {
        queueDepthGauge.decrementAndGet();
        activeTasksGauge.incrementAndGet();
    }

    public void recordTaskCompleted(long durationMs, String taskType) {
        taskCompletedCounter.increment();
        activeTasksGauge.decrementAndGet();
        taskDurationTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void recordTaskFailed(String taskType) {
        taskFailedCounter.increment();
        activeTasksGauge.decrementAndGet();
    }

    public long getActiveTaskCount() {
        return activeTasksGauge.get();
    }

    public long getQueueDepth() {
        return queueDepthGauge.get();
    }
}
