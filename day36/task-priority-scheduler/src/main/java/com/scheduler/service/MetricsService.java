package com.scheduler.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, AtomicInteger> queueDepths;
    private final Timer highPriorityTimer;
    private final Timer normalPriorityTimer;
    private final Timer lowPriorityTimer;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.queueDepths = new ConcurrentHashMap<>();
        
        this.highPriorityTimer = Timer.builder("task.processing.time")
                .tag("priority", "high")
                .register(meterRegistry);
        this.normalPriorityTimer = Timer.builder("task.processing.time")
                .tag("priority", "normal")
                .register(meterRegistry);
        this.lowPriorityTimer = Timer.builder("task.processing.time")
                .tag("priority", "low")
                .register(meterRegistry);
        
        // Initialize queue depth gauges
        meterRegistry.gauge("queue.depth", java.util.List.of(
            io.micrometer.core.instrument.Tag.of("priority", "high")),
            this, m -> m.getQueueDepth("high"));
        meterRegistry.gauge("queue.depth", java.util.List.of(
            io.micrometer.core.instrument.Tag.of("priority", "normal")),
            this, m -> m.getQueueDepth("normal"));
        meterRegistry.gauge("queue.depth", java.util.List.of(
            io.micrometer.core.instrument.Tag.of("priority", "low")),
            this, m -> m.getQueueDepth("low"));
    }
    
    public void recordProcessingTime(String priority, LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        Timer timer = switch (priority.toLowerCase()) {
            case "high" -> highPriorityTimer;
            case "normal" -> normalPriorityTimer;
            default -> lowPriorityTimer;
        };
        timer.record(duration);
    }
    
    public void updateQueueDepth(String priority, int depth) {
        queueDepths.computeIfAbsent(priority, k -> new AtomicInteger(0)).set(depth);
    }
    
    public int getQueueDepth(String priority) {
        return queueDepths.getOrDefault(priority, new AtomicInteger(0)).get();
    }
}
