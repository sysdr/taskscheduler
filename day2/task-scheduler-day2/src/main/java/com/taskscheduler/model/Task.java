package com.taskscheduler.model;

import java.time.LocalDateTime;

public class Task {
    private String id;
    private String name;
    private String description;
    private TaskType type;
    private String schedule;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastRun;
    private LocalDateTime nextRun;
    private TaskStatus status;

    public enum TaskType {
        FIXED_RATE("Fixed Rate"),
        FIXED_DELAY("Fixed Delay"),
        CRON("Cron Expression");

        private final String displayName;

        TaskType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TaskStatus {
        ACTIVE("Active"),
        PAUSED("Paused"),
        ERROR("Error");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.status = TaskStatus.ACTIVE;
        this.active = true;
    }

    public Task(String name, String description, TaskType type, String schedule) {
        this();
        this.name = name;
        this.description = description;
        this.type = type;
        this.schedule = schedule;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    public LocalDateTime getNextRun() {
        return nextRun;
    }

    public void setNextRun(LocalDateTime nextRun) {
        this.nextRun = nextRun;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", schedule='" + schedule + '\'' +
                ", active=" + active +
                ", status=" + status +
                '}';
    }
} 