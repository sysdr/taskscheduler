package com.taskscheduler.service;

import com.taskscheduler.entity.TaskLock;
import com.taskscheduler.repository.TaskLockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseDistributedLockServiceTest {
    
    @Autowired
    private DistributedLockService lockService;
    
    @Autowired
    private TaskLockRepository lockRepository;
    
    @BeforeEach
    void setUp() {
        lockRepository.deleteAll();
    }
    
    @Test
    void shouldAcquireLockSuccessfully() {
        String lockKey = "test-lock";
        Duration holdDuration = Duration.ofMinutes(1);
        
        LockHandle handle = lockService.acquireLock(lockKey, holdDuration);
        
        assertNotNull(handle);
        assertEquals(lockKey, handle.getLockKey());
        assertFalse(handle.isReleased());
        assertTrue(lockService.isLockHeld(lockKey));
        
        handle.release();
        assertTrue(handle.isReleased());
        assertFalse(lockService.isLockHeld(lockKey));
    }
    
    @Test
    void shouldPreventConcurrentLockAcquisition() throws InterruptedException {
        String lockKey = "concurrent-test-lock";
        Duration holdDuration = Duration.ofSeconds(5);
        Duration waitTimeout = Duration.ofSeconds(1);
        
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    LockHandle handle = lockService.acquireLock(lockKey, holdDuration, waitTimeout);
                    if (handle != null) {
                        successCount.incrementAndGet();
                        // Simulate work
                        Thread.sleep(100);
                        handle.release();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Only one thread should succeed in acquiring the lock
        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failureCount.get());
    }
    
    @Test
    void shouldExecuteWithLockAutoManagement() throws Exception {
        String lockKey = "auto-managed-lock";
        Duration holdDuration = Duration.ofMinutes(1);
        Duration waitTimeout = Duration.ofSeconds(5);
        
        AtomicInteger counter = new AtomicInteger(0);
        
        String result = lockService.executeWithLock(lockKey, holdDuration, waitTimeout, () -> {
            counter.incrementAndGet();
            return "success";
        });
        
        assertEquals("success", result);
        assertEquals(1, counter.get());
        assertFalse(lockService.isLockHeld(lockKey));
    }
    
    @Test
    void shouldCleanupExpiredLocks() {
        // Create expired lock manually
        TaskLock expiredLock = new TaskLock("expired-lock", "old-instance", 
            java.time.LocalDateTime.now().minusMinutes(1));
        lockRepository.save(expiredLock);
        
        int cleaned = lockService.cleanupExpiredLocks();
        
        assertEquals(1, cleaned);
        assertFalse(lockService.isLockHeld("expired-lock"));
    }
    
    @Test
    void shouldGetLockStatistics() {
        LockStatistics stats = lockService.getStatistics();
        
        assertNotNull(stats);
        assertTrue(stats.totalActiveLocks() >= 0);
        assertTrue(stats.totalAcquisitions() >= 0);
        assertTrue(stats.totalReleases() >= 0);
    }
}
