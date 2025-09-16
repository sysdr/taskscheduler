package com.taskscheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

@Service
public class CoordinationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CoordinationService.class);
    
    @Autowired
    @Qualifier("primaryLockRegistry")
    private LockRegistry lockRegistry;
    
    @Autowired
    @Qualifier("jdbcLockRegistry")
    private LockRegistry jdbcLockRegistry;
    
    public <T> T executeWithLock(String lockKey, long timeout, TimeUnit timeUnit, 
                                 Supplier<T> task) {
        Lock lock = lockRegistry.obtain(lockKey);
        
        try {
            if (lock.tryLock(timeout, timeUnit)) {
                logger.debug("Acquired lock: {}", lockKey);
                try {
                    return task.get();
                } finally {
                    lock.unlock();
                    logger.debug("Released lock: {}", lockKey);
                }
            } else {
                logger.warn("Failed to acquire lock: {} within {} {}", lockKey, timeout, timeUnit);
                throw new IllegalStateException("Could not acquire lock: " + lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for lock: " + lockKey, e);
        }
    }
    
    public void executeWithLock(String lockKey, long timeout, TimeUnit timeUnit, 
                               Runnable task) {
        executeWithLock(lockKey, timeout, timeUnit, () -> {
            task.run();
            return null;
        });
    }
    
    public boolean tryExecuteWithLock(String lockKey, Runnable task) {
        Lock lock = lockRegistry.obtain(lockKey);
        
        if (lock.tryLock()) {
            try {
                logger.debug("Acquired immediate lock: {}", lockKey);
                task.run();
                return true;
            } finally {
                lock.unlock();
                logger.debug("Released immediate lock: {}", lockKey);
            }
        } else {
            logger.debug("Could not acquire immediate lock: {}", lockKey);
            return false;
        }
    }
    
    public <T> T executeWithJdbcLock(String lockKey, long timeout, TimeUnit timeUnit,
                                     Supplier<T> task) {
        Lock lock = jdbcLockRegistry.obtain(lockKey);
        
        try {
            if (lock.tryLock(timeout, timeUnit)) {
                logger.debug("Acquired JDBC lock: {}", lockKey);
                try {
                    return task.get();
                } finally {
                    lock.unlock();
                    logger.debug("Released JDBC lock: {}", lockKey);
                }
            } else {
                logger.warn("Failed to acquire JDBC lock: {} within {} {}", lockKey, timeout, timeUnit);
                throw new IllegalStateException("Could not acquire JDBC lock: " + lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for JDBC lock: " + lockKey, e);
        }
    }
}
