package com.taskscheduler.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class TaskExecutionMetrics {
    private final String taskName;
    private final AtomicLong executionCount = new AtomicLong(0);
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    private volatile LocalDateTime lastExecution;
    private volatile String lastExecutingThread;
    private volatile long lastExecutionDuration;
    
    public TaskExecutionMetrics(String taskName) {
        this.taskName = taskName;
    }
    
    public void recordExecution(long durationMs, String threadName) {
        executionCount.incrementAndGet();
        totalExecutionTime.addAndGet(durationMs);
        lastExecution = LocalDateTime.now();
        lastExecutingThread = threadName;
        lastExecutionDuration = durationMs;
    }
    
    public double getAverageExecutionTime() {
        long count = executionCount.get();
        return count > 0 ? (double) totalExecutionTime.get() / count : 0.0;
    }
    
    // Getters
    public String getTaskName() { return taskName; }
    public long getExecutionCount() { return executionCount.get(); }
    public long getTotalExecutionTime() { return totalExecutionTime.get(); }
    public LocalDateTime getLastExecution() { return lastExecution; }
    public String getLastExecutingThread() { return lastExecutingThread; }
    public long getLastExecutionDuration() { return lastExecutionDuration; }
}
