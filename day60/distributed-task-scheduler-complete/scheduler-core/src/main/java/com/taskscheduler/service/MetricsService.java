package com.taskscheduler.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.taskscheduler.domain.Task;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry meterRegistry;
    
    public void recordTasksScheduled(int count) {
        meterRegistry.counter("tasks.scheduled.total").increment(count);
    }
    
    public void recordTaskDispatched(Task.TaskPriority priority) {
        meterRegistry.counter("tasks.dispatched", "priority", priority.name()).increment();
    }
    
    public void recordTaskSuccess(long durationMs) {
        meterRegistry.counter("tasks.completed.success").increment();
        meterRegistry.timer("tasks.execution.duration").record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    public void recordTaskFailed() {
        meterRegistry.counter("tasks.completed.failed").increment();
    }
    
    public void recordSchedulerError() {
        meterRegistry.counter("scheduler.errors").increment();
    }
    }
