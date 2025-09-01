package com.taskscheduler.service;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * Service for managing distributed locks across multiple application instances.
 * Uses database-backed pessimistic locking for guaranteed mutual exclusion.
 */
public interface DistributedLockService {
    
    /**
     * Attempt to acquire a distributed lock
     * @param lockKey Unique identifier for the lock
     * @param holdDuration How long to hold the lock
     * @param waitTimeout How long to wait if lock is unavailable
     * @return LockHandle if successful, empty if failed
     */
    LockHandle acquireLock(String lockKey, Duration holdDuration, Duration waitTimeout);
    
    /**
     * Acquire lock with default timeout
     */
    LockHandle acquireLock(String lockKey, Duration holdDuration);
    
    /**
     * Release a previously acquired lock
     */
    boolean releaseLock(LockHandle lockHandle);
    
    /**
     * Execute code with automatic lock management
     */
    <T> T executeWithLock(String lockKey, Duration holdDuration, Duration waitTimeout, Callable<T> task) throws Exception;
    
    /**
     * Execute code with automatic lock management (void return)
     */
    void executeWithLock(String lockKey, Duration holdDuration, Duration waitTimeout, Runnable task);
    
    /**
     * Check if a lock is currently held
     */
    boolean isLockHeld(String lockKey);
    
    /**
     * Get information about a lock
     */
    LockInfo getLockInfo(String lockKey);
    
    /**
     * Force release of expired locks (cleanup operation)
     */
    int cleanupExpiredLocks();
    
    /**
     * Get current lock statistics
     */
    LockStatistics getStatistics();
}
