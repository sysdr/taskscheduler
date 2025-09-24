package com.taskscheduler.dto;

import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.Set;

public class TaskStatusResponse {
    
    private Long id;
    private String taskName;
    private TaskStatus status;
    private String statusDescription;
    private Set<TaskStatus> validNextStatuses;
    private String executionDetails;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationMs;
    private boolean isTerminal;
    private boolean isActive;
    private boolean isCompleted;
    
    public static TaskStatusResponse from(TaskExecution task) {
        TaskStatusResponse response = new TaskStatusResponse();
        response.id = task.getId();
        response.taskName = task.getTaskName();
        response.status = task.getStatus();
        response.statusDescription = task.getStatus().getDescription();
        response.validNextStatuses = task.getStatus().getValidNextStatuses();
        response.executionDetails = task.getExecutionDetails();
        response.errorMessage = task.getErrorMessage();
        response.retryCount = task.getRetryCount();
        response.createdAt = task.getCreatedAt();
        response.updatedAt = task.getUpdatedAt();
        response.startedAt = task.getStartedAt();
        response.completedAt = task.getCompletedAt();
        response.durationMs = task.getDurationMs();
        response.isTerminal = task.isTerminal();
        response.isActive = task.isActive();
        response.isCompleted = task.isCompleted();
        return response;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTaskName() { return taskName; }
    public TaskStatus getStatus() { return status; }
    public String getStatusDescription() { return statusDescription; }
    public Set<TaskStatus> getValidNextStatuses() { return validNextStatuses; }
    public String getExecutionDetails() { return executionDetails; }
    public String getErrorMessage() { return errorMessage; }
    public Integer getRetryCount() { return retryCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Long getDurationMs() { return durationMs; }
    public boolean isTerminal() { return isTerminal; }
    public boolean isActive() { return isActive; }
    public boolean isCompleted() { return isCompleted; }
}
