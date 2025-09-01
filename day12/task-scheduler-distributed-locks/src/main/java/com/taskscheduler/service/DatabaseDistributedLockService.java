package com.taskscheduler.service;

import com.taskscheduler.entity.TaskLock;
import com.taskscheduler.repository.TaskLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DatabaseDistributedLockService implements DistributedLockService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDistributedLockService.class);
    
    private final TaskLockRepository lockRepository;
    private final String instanceId;
    
    // Statistics
    private final AtomicLong acquisitionCount = new AtomicLong(0);
    private final AtomicLong releaseCount = new AtomicLong(0);
    private final AtomicLong timeoutCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    
    @Value("${app.distributed-locks.default-timeout-seconds:30}")
    private long defaultTimeoutSeconds;
    
    @Value("${app.distributed-locks.max-wait-seconds:10}")
    private long maxWaitSeconds;
    
    public DatabaseDistributedLockService(TaskLockRepository lockRepository) {
        this.lockRepository = lockRepository;
        this.instanceId = generateInstanceId();
        logger.info("Initialized DistributedLockService with instanceId: {}", instanceId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public LockHandle acquireLock(String lockKey, Duration holdDuration, Duration waitTimeout) {
        logger.debug("Attempting to acquire lock: {} for duration: {} with timeout: {}", 
                    lockKey, holdDuration, waitTimeout);
        
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime deadline = startTime.plus(waitTimeout);
        LocalDateTime expiresAt = startTime.plus(holdDuration);
        
        while (LocalDateTime.now().isBefore(deadline)) {
            try {
                // Try to acquire the lock
                Optional<TaskLock> existingLock = lockRepository.findByLockKeyForUpdate(lockKey);
                
                if (existingLock.isEmpty()) {
                    // No existing lock, create new one
                    TaskLock newLock = new TaskLock(lockKey, instanceId, expiresAt);
                    lockRepository.save(newLock);
                    
                    LockHandle handle = new LockHandle(lockKey, instanceId, startTime, expiresAt, this);
                    acquisitionCount.incrementAndGet();
                    logger.debug("Successfully acquired new lock: {}", lockKey);
                    return handle;
                    
                } else {
                    TaskLock lock = existingLock.get();
                    
                    // Check if existing lock is expired
                    if (lock.isExpired()) {
                        // Take over expired lock
                        lock.setOwnerInstance(instanceId);
                        lock.setExpiresAt(expiresAt);
                        lockRepository.save(lock);
                        
                        LockHandle handle = new LockHandle(lockKey, instanceId, startTime, expiresAt, this);
                        acquisitionCount.incrementAndGet();
                        logger.debug("Successfully acquired expired lock: {}", lockKey);
                        return handle;
                    }
                    
                    // Lock is held by another instance, wait briefly and retry
                    logger.trace("Lock {} is held by {}, waiting...", lockKey, lock.getOwnerInstance());
                }
                
                // Wait before retry
                try {
                    Thread.sleep(100); // 100ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
            } catch (DataIntegrityViolationException e) {
                // Unique constraint violation - another thread acquired the lock
                logger.trace("Concurrent acquisition detected for lock: {}", lockKey);
                try {
                    Thread.sleep(50); // Brief wait before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                logger.error("Unexpected error acquiring lock: {}", lockKey, e);
                failureCount.incrementAndGet();
                break;
            }
        }
        
        timeoutCount.incrementAndGet();
        logger.debug("Failed to acquire lock: {} within timeout: {}", lockKey, waitTimeout);
        return null;
    }
    
    @Override
    public LockHandle acquireLock(String lockKey, Duration holdDuration) {
        return acquireLock(lockKey, holdDuration, Duration.ofSeconds(maxWaitSeconds));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean releaseLock(LockHandle lockHandle) {
        if (lockHandle == null || lockHandle.isReleased()) {
            return true;
        }
        
        try {
            int deleted = lockRepository.deleteByLockKeyAndOwner(
                lockHandle.getLockKey(), 
                lockHandle.getOwnerInstance()
            );
            
            if (deleted > 0) {
                lockHandle.markReleased();
                releaseCount.incrementAndGet();
                logger.debug("Successfully released lock: {}", lockHandle.getLockKey());
                return true;
            } else {
                logger.warn("Failed to release lock - not found or not owned: {}", lockHandle.getLockKey());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error releasing lock: {}", lockHandle.getLockKey(), e);
            failureCount.incrementAndGet();
            return false;
        }
    }
    
    @Override
    public <T> T executeWithLock(String lockKey, Duration holdDuration, Duration waitTimeout, Callable<T> task) throws Exception {
        LockHandle lock = acquireLock(lockKey, holdDuration, waitTimeout);
        if (lock == null) {
            throw new IllegalStateException("Failed to acquire lock: " + lockKey);
        }
        
        try (lock) {
            return task.call();
        }
    }
    
    @Override
    public void executeWithLock(String lockKey, Duration holdDuration, Duration waitTimeout, Runnable task) {
        try {
            executeWithLock(lockKey, holdDuration, waitTimeout, () -> {
                task.run();
                return null;
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error executing task with lock", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isLockHeld(String lockKey) {
        return lockRepository.existsActiveLock(lockKey, LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public LockInfo getLockInfo(String lockKey) {
        Optional<TaskLock> lock = lockRepository.findByLockKey(lockKey);
        
        if (lock.isEmpty()) {
            return LockInfo.notFound(lockKey);
        }
        
        TaskLock taskLock = lock.get();
        return new LockInfo(
            taskLock.getLockKey(),
            taskLock.getOwnerInstance(),
            taskLock.getAcquiredAt(),
            taskLock.getExpiresAt(),
            taskLock.getTaskType(),
            taskLock.getDescription(),
            taskLock.isExpired()
        );
    }
    
    @Override
    @Transactional
    public int cleanupExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = lockRepository.deleteExpiredLocks(now);
        
        if (deleted > 0) {
            logger.info("Cleaned up {} expired locks", deleted);
        }
        
        return deleted;
    }
    
    @Override
    public LockStatistics getStatistics() {
        long activeLocks = lockRepository.countActiveLocks(LocalDateTime.now());
        
        return new LockStatistics(
            activeLocks,
            acquisitionCount.get(),
            releaseCount.get(),
            timeoutCount.get(),
            failureCount.get(),
            0.0 // TODO: Implement average hold time calculation
        );
    }
    
    private String generateInstanceId() {
        return "instance-" + System.currentTimeMillis() + "-" + 
               Thread.currentThread().getId() + "-" + 
               System.nanoTime() % 10000;
    }
}
