package com.scheduler.backpressure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {
    private final Counter tasksProcessed;
    private final Counter tasksThrottled;
    private final Timer processingTime;
    private final AtomicLong currentRate = new AtomicLong(0);
    private final AtomicLong queueDepth = new AtomicLong(0);
    
    public MetricsService(MeterRegistry registry) {
        this.tasksProcessed = Counter.builder("tasks.processed")
            .description("Total tasks processed")
            .register(registry);
            
        this.tasksThrottled = Counter.builder("tasks.throttled")
            .description("Tasks throttled due to rate limiting")
            .register(registry);
            
        this.processingTime = Timer.builder("task.processing.time")
            .description("Time taken to process task")
            .register(registry);
            
        registry.gauge("tasks.current.rate", currentRate);
        registry.gauge("queue.depth", queueDepth);
    }
    
    public void recordTaskProcessed() {
        tasksProcessed.increment();
    }
    
    public void recordTaskThrottled() {
        tasksThrottled.increment();
    }
    
    public void recordProcessingTime(Duration duration) {
        processingTime.record(duration);
    }
    
    public void updateCurrentRate(long rate) {
        currentRate.set(rate);
    }
    
    public void updateQueueDepth(long depth) {
        queueDepth.set(depth);
    }
    
    public long getTasksProcessedCount() {
        return (long) tasksProcessed.count();
    }
    
    public long getTasksThrottledCount() {
        return (long) tasksThrottled.count();
    }
}
