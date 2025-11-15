package com.taskscheduler.model;

import lombok.Data;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class EventMetrics {
    private AtomicLong fileEventsReceived = new AtomicLong(0);
    private AtomicLong userEventsReceived = new AtomicLong(0);
    private AtomicLong systemEventsReceived = new AtomicLong(0);
    private AtomicLong tasksTriggered = new AtomicLong(0);
    private AtomicLong tasksCompleted = new AtomicLong(0);
    private AtomicLong tasksFailed = new AtomicLong(0);
    private AtomicLong deadLetterEvents = new AtomicLong(0);
}
