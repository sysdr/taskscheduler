package com.scheduler.dto;

import java.util.Map;

public class DashboardStats {
    private long totalTasks;
    private long createdTasks;
    private long processingTasks;
    private long completedTasks;
    private long retryingTasks;
    private long deadLetterTasks;
    private Map<String, Long> failureReasonStats;
    
    // Constructors
    public DashboardStats() {}
    
    // Getters and Setters
    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
    
    public long getCreatedTasks() { return createdTasks; }
    public void setCreatedTasks(long createdTasks) { this.createdTasks = createdTasks; }
    
    public long getProcessingTasks() { return processingTasks; }
    public void setProcessingTasks(long processingTasks) { this.processingTasks = processingTasks; }
    
    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }
    
    public long getRetryingTasks() { return retryingTasks; }
    public void setRetryingTasks(long retryingTasks) { this.retryingTasks = retryingTasks; }
    
    public long getDeadLetterTasks() { return deadLetterTasks; }
    public void setDeadLetterTasks(long deadLetterTasks) { this.deadLetterTasks = deadLetterTasks; }
    
    public Map<String, Long> getFailureReasonStats() { return failureReasonStats; }
    public void setFailureReasonStats(Map<String, Long> failureReasonStats) { this.failureReasonStats = failureReasonStats; }
}
