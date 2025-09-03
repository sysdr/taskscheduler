package com.scheduler.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    
    private final Counter tasksProcessedCounter;
    private final Counter tasksFailedCounter;
    private final Counter optimisticLockConflictsCounter;
    private final Counter optimisticLockSuccessCounter;
    private final Timer taskProcessingTimer;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.tasksProcessedCounter = Counter.builder("tasks.processed.total")
                .description("Total number of tasks processed successfully")
                .register(meterRegistry);
                
        this.tasksFailedCounter = Counter.builder("tasks.failed.total")
                .description("Total number of tasks that failed processing")
                .register(meterRegistry);
                
        this.optimisticLockConflictsCounter = Counter.builder("optimistic.lock.conflicts.total")
                .description("Total number of optimistic lock conflicts")
                .register(meterRegistry);
                
        this.optimisticLockSuccessCounter = Counter.builder("optimistic.lock.success.total")
                .description("Total number of successful optimistic lock acquisitions")
                .register(meterRegistry);
                
        this.taskProcessingTimer = Timer.builder("task.processing.duration")
                .description("Time taken to process tasks")
                .register(meterRegistry);
    }
    
    public void incrementTasksProcessed() {
        tasksProcessedCounter.increment();
    }
    
    public void incrementTasksFailed() {
        tasksFailedCounter.increment();
    }
    
    public void incrementOptimisticLockConflicts() {
        optimisticLockConflictsCounter.increment();
    }
    
    public void incrementOptimisticLockSuccess() {
        optimisticLockSuccessCounter.increment();
    }
    
    public Timer.Sample startProcessingTimer() {
        return Timer.start();
    }
    
    public void stopProcessingTimer(Timer.Sample sample) {
        sample.stop(taskProcessingTimer);
    }
}
