package com.taskscheduler.dto;

public class ExecutionStatsDto {
    private Long pendingCount;
    private Long runningCount;
    private Long successCount;
    private Long failedCount;
    private Double averageExecutionTimeMs;
    private Double successRate;
    
    // Constructors
    public ExecutionStatsDto() {}
    
    // Getters and Setters
    public Long getPendingCount() { return pendingCount; }
    public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
    
    public Long getRunningCount() { return runningCount; }
    public void setRunningCount(Long runningCount) { this.runningCount = runningCount; }
    
    public Long getSuccessCount() { return successCount; }
    public void setSuccessCount(Long successCount) { this.successCount = successCount; }
    
    public Long getFailedCount() { return failedCount; }
    public void setFailedCount(Long failedCount) { this.failedCount = failedCount; }
    
    public Double getAverageExecutionTimeMs() { return averageExecutionTimeMs; }
    public void setAverageExecutionTimeMs(Double averageExecutionTimeMs) { 
        this.averageExecutionTimeMs = averageExecutionTimeMs; 
    }
    
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
}
