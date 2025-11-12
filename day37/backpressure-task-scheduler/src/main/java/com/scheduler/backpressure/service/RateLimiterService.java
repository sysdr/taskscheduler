package com.scheduler.backpressure.service;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    private final RateLimiter rateLimiter;
    private final int maxPermitsPerSecond = 10;
    private boolean enabled = true;
    
    public RateLimiterService() {
        // Allow 10 permits per second with burst capacity
        this.rateLimiter = RateLimiter.create(maxPermitsPerSecond);
    }
    
    public boolean tryAcquire() {
        if (!enabled) {
            return true;
        }
        return rateLimiter.tryAcquire();
    }
    
    public void acquire() {
        if (enabled) {
            rateLimiter.acquire();
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public int getMaxPermitsPerSecond() {
        return maxPermitsPerSecond;
    }
}
