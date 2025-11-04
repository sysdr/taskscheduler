package com.taskscheduler.consumer.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ConsumerMetrics {
    private final Counter successfulTasks;
    private final Counter failedTasks;
    private final Counter retriedTasks;
    private final Timer processingTimer;

    public ConsumerMetrics(MeterRegistry meterRegistry) {
        this.successfulTasks = Counter.builder("task.consumer.successful")
                .description("Number of successfully processed tasks")
                .register(meterRegistry);
                
        this.failedTasks = Counter.builder("task.consumer.failed")
                .description("Number of permanently failed tasks")
                .register(meterRegistry);
                
        this.retriedTasks = Counter.builder("task.consumer.retried")
                .description("Number of retried tasks")
                .register(meterRegistry);
                
        this.processingTimer = Timer.builder("task.consumer.processing.time")
                .description("Task processing time")
                .register(meterRegistry);
    }

    public void incrementSuccessfulTasks() {
        successfulTasks.increment();
    }

    public void incrementFailedTasks() {
        failedTasks.increment();
    }

    public void incrementRetriedTasks() {
        retriedTasks.increment();
    }

    public void recordProcessingTime(long timeMs) {
        processingTimer.record(timeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // Getters for dashboard
    public double getSuccessfulTasksCount() { return successfulTasks.count(); }
    public double getFailedTasksCount() { return failedTasks.count(); }
    public double getRetriedTasksCount() { return retriedTasks.count(); }
    public double getAverageProcessingTime() { return processingTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS); }
}
