package com.taskscheduler.alerting.model;

public enum AlertStatus {
    FIRING,     // Alert is active
    RESOLVED,   // Issue resolved
    SILENCED,   // Temporarily muted
    ACKNOWLEDGED // Team aware, working on it
}
