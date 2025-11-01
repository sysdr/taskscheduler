package com.taskscheduler.model;

public enum TaskStatus {
    SCHEDULED("Scheduled"),
    QUEUED("Queued"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    RETRYING("Retrying"),
    DEAD_LETTER("Dead Letter");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
