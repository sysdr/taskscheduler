package com.scheduler.chaos.model;

import lombok.Data;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class ChaosMetrics {
    private final AtomicLong tasksProcessed = new AtomicLong(0);
    private final AtomicLong tasksFailed = new AtomicLong(0);
    private final AtomicLong leaderElections = new AtomicLong(0);
    private final AtomicLong circuitBreakerTrips = new AtomicLong(0);
    private volatile long avgLatency = 0;
    private volatile boolean chaosActive = false;
    
    public void recordTask(boolean success, long latency) {
        if (success) {
            tasksProcessed.incrementAndGet();
        } else {
            tasksFailed.incrementAndGet();
        }
        updateAvgLatency(latency);
    }
    
    private void updateAvgLatency(long latency) {
        this.avgLatency = (this.avgLatency + latency) / 2;
    }
    
    public double getErrorRate() {
        long total = tasksProcessed.get() + tasksFailed.get();
        return total > 0 ? (tasksFailed.get() * 100.0 / total) : 0.0;
    }
}
