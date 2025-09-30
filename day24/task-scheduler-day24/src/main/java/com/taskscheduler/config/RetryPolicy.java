package com.taskscheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "task-scheduler.retry")
public class RetryPolicy {
    private int maxAttempts = 3;
    private long baseDelayMs = 1000;
    private long maxDelayMs = 30000;
    private double exponentialMultiplier = 2.0;
    private double jitterFactor = 0.1;
    
    // Getters and Setters
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public long getBaseDelayMs() { return baseDelayMs; }
    public void setBaseDelayMs(long baseDelayMs) { this.baseDelayMs = baseDelayMs; }
    
    public long getMaxDelayMs() { return maxDelayMs; }
    public void setMaxDelayMs(long maxDelayMs) { this.maxDelayMs = maxDelayMs; }
    
    public double getExponentialMultiplier() { return exponentialMultiplier; }
    public void setExponentialMultiplier(double exponentialMultiplier) { 
        this.exponentialMultiplier = exponentialMultiplier; 
    }
    
    public double getJitterFactor() { return jitterFactor; }
    public void setJitterFactor(double jitterFactor) { this.jitterFactor = jitterFactor; }
    
    public long calculateBackoffDelay(int attemptCount) {
        double delay = baseDelayMs * Math.pow(exponentialMultiplier, attemptCount);
        
        // Apply maximum delay limit
        delay = Math.min(delay, maxDelayMs);
        
        // Add jitter to prevent thundering herd
        double jitter = delay * jitterFactor * Math.random();
        
        return (long) (delay + jitter);
    }
}
