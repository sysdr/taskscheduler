package com.scheduler.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
public class PerformanceMetrics {
    private final Counter tasksProcessed;
    private final Counter tasksFailed;
    private final Timer taskExecutionTime;
    private final LongAdder activeTasksCounter = new LongAdder();
    private final AtomicLong heapUsage = new AtomicLong(0);

    public PerformanceMetrics(MeterRegistry registry) {
        this.tasksProcessed = Counter.builder("tasks.processed")
            .description("Total tasks processed")
            .register(registry);
            
        this.tasksFailed = Counter.builder("tasks.failed")
            .description("Total tasks failed")
            .register(registry);
            
        this.taskExecutionTime = Timer.builder("tasks.execution.time")
            .description("Task execution time")
            .register(registry);

        registry.gauge("tasks.active", activeTasksCounter, LongAdder::sum);
        registry.gauge("jvm.heap.used", heapUsage, AtomicLong::get);
    }

    public void recordTaskProcessed() {
        tasksProcessed.increment();
    }

    public void recordTaskFailed() {
        tasksFailed.increment();
    }

    public Timer.Sample startTimer() {
        activeTasksCounter.increment();
        return Timer.start();
    }

    public void recordExecutionTime(Timer.Sample sample) {
        sample.stop(taskExecutionTime);
        activeTasksCounter.decrement();
    }

    public void updateHeapUsage() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        heapUsage.set(used);
    }

    public long getTasksProcessedCount() {
        return (long) tasksProcessed.count();
    }

    public long getActiveTasksCount() {
        return activeTasksCounter.sum();
    }
}
