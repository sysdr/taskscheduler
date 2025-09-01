package com.taskscheduler.service;

import java.time.LocalDateTime;

/**
 * Handle representing an acquired distributed lock.
 * Implements AutoCloseable for automatic resource management.
 */
public class LockHandle implements AutoCloseable {
    
    private final String lockKey;
    private final String ownerInstance;
    private final LocalDateTime acquiredAt;
    private final LocalDateTime expiresAt;
    private final DistributedLockService lockService;
    private volatile boolean released = false;
    
    public LockHandle(String lockKey, String ownerInstance, LocalDateTime acquiredAt, 
                     LocalDateTime expiresAt, DistributedLockService lockService) {
        this.lockKey = lockKey;
        this.ownerInstance = ownerInstance;
        this.acquiredAt = acquiredAt;
        this.expiresAt = expiresAt;
        this.lockService = lockService;
    }
    
    public String getLockKey() { return lockKey; }
    public String getOwnerInstance() { return ownerInstance; }
    public LocalDateTime getAcquiredAt() { return acquiredAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isReleased() { return released; }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public synchronized boolean release() {
        if (!released) {
            released = lockService.releaseLock(this);
            return released;
        }
        return true;
    }
    
    @Override
    public void close() {
        release();
    }
    
    void markReleased() {
        this.released = true;
    }
    
    @Override
    public String toString() {
        return "LockHandle{" +
                "lockKey='" + lockKey + '\'' +
                ", ownerInstance='" + ownerInstance + '\'' +
                ", acquiredAt=" + acquiredAt +
                ", expiresAt=" + expiresAt +
                ", released=" + released +
                ", expired=" + isExpired() +
                '}';
    }
}
