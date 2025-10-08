package com.taskscheduler.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskMetrics {
    
    private final Counter taskStartedCounter;
    private final Counter taskCompletedCounter;
    private final Counter taskFailedCounter;
    private final Counter taskRetriedCounter;
    private final Timer taskExecutionTimer;
    
    @Autowired
    public TaskMetrics(MeterRegistry meterRegistry) {
        this.taskStartedCounter = Counter.builder("tasks.started")
                .description("Total number of tasks started")
                .register(meterRegistry);
                
        this.taskCompletedCounter = Counter.builder("tasks.completed")
                .description("Total number of tasks completed successfully")
                .register(meterRegistry);
                
        this.taskFailedCounter = Counter.builder("tasks.failed")
                .description("Total number of tasks that failed")
                .register(meterRegistry);
                
        this.taskRetriedCounter = Counter.builder("tasks.retried")
                .description("Total number of task retry attempts")
                .register(meterRegistry);
                
        this.taskExecutionTimer = Timer.builder("tasks.execution.time")
                .description("Task execution time")
                .register(meterRegistry);
    }
    
    public void incrementTasksStarted() {
        taskStartedCounter.increment();
    }
    
    public void incrementTasksCompleted() {
        taskCompletedCounter.increment();
    }
    
    public void incrementTasksFailed() {
        taskFailedCounter.increment();
    }
    
    public void incrementTasksRetried() {
        taskRetriedCounter.increment();
    }
    
    public Timer.Sample startTaskTimer() {
        return Timer.start();
    }
    
    public void stopTaskTimer(Timer.Sample sample) {
        sample.stop(taskExecutionTimer);
    }
}
