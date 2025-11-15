package com.taskscheduler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemHealthEvent extends BaseEvent {
    private String metricType; // CPU_HIGH, DISK_LOW, MEMORY_HIGH, etc.
    private double currentValue;
    private double threshold;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    public SystemHealthEvent() {
        setEventType("SYSTEM_HEALTH");
        setSource("MONITORING_SYSTEM");
    }
}
