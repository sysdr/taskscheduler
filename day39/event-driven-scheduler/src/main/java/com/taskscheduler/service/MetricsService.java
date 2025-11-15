package com.taskscheduler.service;

import com.taskscheduler.model.EventMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    
    @Getter
    private final EventMetrics eventMetrics = new EventMetrics();
    
    private final Counter fileEventsCounter;
    private final Counter userEventsCounter;
    private final Counter systemEventsCounter;
    private final Counter tasksTriggeredCounter;
    private final Counter tasksCompletedCounter;
    private final Counter tasksFailedCounter;
    private final Counter deadLetterCounter;

    public MetricsService(MeterRegistry meterRegistry) {
        this.fileEventsCounter = Counter.builder("events.file.received")
                .description("Number of file upload events received")
                .register(meterRegistry);
        
        this.userEventsCounter = Counter.builder("events.user.received")
                .description("Number of user action events received")
                .register(meterRegistry);
        
        this.systemEventsCounter = Counter.builder("events.system.received")
                .description("Number of system health events received")
                .register(meterRegistry);
        
        this.tasksTriggeredCounter = Counter.builder("tasks.triggered")
                .description("Number of tasks triggered by events")
                .register(meterRegistry);
        
        this.tasksCompletedCounter = Counter.builder("tasks.completed")
                .description("Number of tasks completed successfully")
                .register(meterRegistry);
        
        this.tasksFailedCounter = Counter.builder("tasks.failed")
                .description("Number of tasks that failed")
                .register(meterRegistry);
        
        this.deadLetterCounter = Counter.builder("events.deadletter")
                .description("Number of events routed to dead letter queue")
                .register(meterRegistry);
    }

    public void incrementFileEvents() {
        eventMetrics.getFileEventsReceived().incrementAndGet();
        fileEventsCounter.increment();
    }

    public void incrementUserEvents() {
        eventMetrics.getUserEventsReceived().incrementAndGet();
        userEventsCounter.increment();
    }

    public void incrementSystemEvents() {
        eventMetrics.getSystemEventsReceived().incrementAndGet();
        systemEventsCounter.increment();
    }

    public void incrementTasksTriggered() {
        eventMetrics.getTasksTriggered().incrementAndGet();
        tasksTriggeredCounter.increment();
    }

    public void incrementTasksCompleted() {
        eventMetrics.getTasksCompleted().incrementAndGet();
        tasksCompletedCounter.increment();
    }

    public void incrementTasksFailed() {
        eventMetrics.getTasksFailed().incrementAndGet();
        tasksFailedCounter.increment();
    }

    public void incrementDeadLetterEvents() {
        eventMetrics.getDeadLetterEvents().incrementAndGet();
        deadLetterCounter.increment();
    }
}
