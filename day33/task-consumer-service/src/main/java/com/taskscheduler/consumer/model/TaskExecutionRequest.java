package com.taskscheduler.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskExecutionRequest {
    @JsonProperty("taskId")
    private String taskId;
    
    @JsonProperty("taskType")
    private String taskType;
    
    @JsonProperty("payload")
    private String payload;
    
    @JsonProperty("scheduledTime")
    private String scheduledTime;
    
    // Constructors
    public TaskExecutionRequest() {}
    
    public TaskExecutionRequest(String taskId, String taskType, String payload, String scheduledTime) {
        this.taskId = taskId;
        this.taskType = taskType;
        this.payload = payload;
        this.scheduledTime = scheduledTime;
    }
    
    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    
    @Override
    public String toString() {
        return "TaskExecutionRequest{" +
                "taskId='" + taskId + '\'' +
                ", taskType='" + taskType + '\'' +
                ", payload='" + payload + '\'' +
                ", scheduledTime='" + scheduledTime + '\'' +
                '}';
    }
}
