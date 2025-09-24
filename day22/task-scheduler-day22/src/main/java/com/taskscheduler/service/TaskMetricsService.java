package com.taskscheduler.service;

import com.taskscheduler.enums.TaskStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for recording task execution metrics
 */
@Service
public class TaskMetricsService {
    
    private final Counter taskCreatedCounter;
    private final Counter taskFailedCounter;
    private final Counter transitionCounter;
    private final Timer executionTimer;
    
    @Autowired
    public TaskMetricsService(MeterRegistry meterRegistry) {
        this.taskCreatedCounter = Counter.builder("tasks.created")
            .description("Number of tasks created")
            .register(meterRegistry);
            
        this.taskFailedCounter = Counter.builder("tasks.failed")
            .description("Number of tasks that failed")
            .register(meterRegistry);
            
        this.transitionCounter = Counter.builder("tasks.transitions")
            .description("Number of status transitions")
            .register(meterRegistry);
            
        this.executionTimer = Timer.builder("tasks.execution.duration")
            .description("Task execution duration")
            .register(meterRegistry);
    }
    
    public void recordTaskCreation() {
        taskCreatedCounter.increment();
    }
    
    public void recordTaskFailure() {
        taskFailedCounter.increment();
    }
    
    public void recordStatusTransition(TaskStatus from, TaskStatus to) {
        transitionCounter.increment();
    }
    
    public void recordTaskCompletion(Long durationMs) {
        if (durationMs != null) {
            executionTimer.record(durationMs, TimeUnit.MILLISECONDS);
        }
    }
}
