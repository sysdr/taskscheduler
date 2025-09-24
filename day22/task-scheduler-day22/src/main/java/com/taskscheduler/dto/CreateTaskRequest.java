package com.taskscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {
    
    @NotBlank(message = "Task name is required")
    @Size(min = 1, max = 255, message = "Task name must be between 1 and 255 characters")
    private String taskName;
    
    @Size(max = 1000, message = "Execution details must not exceed 1000 characters")
    private String executionDetails;
    
    public CreateTaskRequest() {}
    
    public CreateTaskRequest(String taskName, String executionDetails) {
        this.taskName = taskName;
        this.executionDetails = executionDetails;
    }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getExecutionDetails() { return executionDetails; }
    public void setExecutionDetails(String executionDetails) { this.executionDetails = executionDetails; }
}
