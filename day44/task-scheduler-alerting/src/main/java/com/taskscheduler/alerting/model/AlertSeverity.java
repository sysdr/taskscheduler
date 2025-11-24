package com.taskscheduler.alerting.model;

public enum AlertSeverity {
    CRITICAL,  // Immediate action required
    HIGH,      // Action required soon
    MEDIUM,    // Investigate during business hours
    LOW        // Informational
}
