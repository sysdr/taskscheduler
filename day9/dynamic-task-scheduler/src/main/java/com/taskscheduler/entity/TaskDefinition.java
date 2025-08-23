package com.taskscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_definitions")
public class TaskDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Task name is required")
    @Column(unique = true)
    private String taskName;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Cron expression is required")
    private String cronExpression;
    
    @NotNull(message = "Task type is required")
    @Enumerated(EnumType.STRING)
    private TaskType taskType;
    
    @Column(columnDefinition = "TEXT")
    private String taskData;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.INACTIVE;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime lastExecuted;
    private LocalDateTime nextExecution;
    
    // Constructors
    public TaskDefinition() {}
    
    public TaskDefinition(String taskName, String description, String cronExpression, TaskType taskType) {
        this.taskName = taskName;
        this.description = description;
        this.cronExpression = cronExpression;
        this.taskType = taskType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    
    public String getTaskData() { return taskData; }
    public void setTaskData(String taskData) { this.taskData = taskData; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastExecuted() { return lastExecuted; }
    public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    
    public LocalDateTime getNextExecution() { return nextExecution; }
    public void setNextExecution(LocalDateTime nextExecution) { this.nextExecution = nextExecution; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum TaskType {
        LOG_MESSAGE,
        EMAIL_NOTIFICATION,
        DATA_CLEANUP,
        REPORT_GENERATION,
        SYSTEM_HEALTH_CHECK
    }
    
    public enum TaskStatus {
        ACTIVE,
        INACTIVE,
        PAUSED,
        ERROR
    }
}
