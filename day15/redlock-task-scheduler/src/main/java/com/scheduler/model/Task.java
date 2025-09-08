package com.scheduler.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDateTime scheduledTime;
    private LocalDateTime executedTime;
    private String executedBy;
    private String lockValue;

    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED
    }

    public Task() {}

    public Task(String id, String name, String description, LocalDateTime scheduledTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.scheduledTime = scheduledTime;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public LocalDateTime getExecutedTime() { return executedTime; }
    public void setExecutedTime(LocalDateTime executedTime) { this.executedTime = executedTime; }
    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }
    public String getLockValue() { return lockValue; }
    public void setLockValue(String lockValue) { this.lockValue = lockValue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
