package com.scheduler.integration;

import com.scheduler.lock.DistributedLock;
import com.scheduler.util.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Testcontainers
class DistributedLockIT {
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @Autowired
    private DistributedLock lock;

    @Test
    void testBasicLock() {
        String key = "test-lock";
        String id = "inst-1";
        
        assertTrue(lock.tryLock(key, id, Duration.ofSeconds(5)));
        assertTrue(lock.isLocked(key));
        assertEquals(id, lock.getLockOwner(key));
        assertTrue(lock.unlock(key, id));
        assertFalse(lock.isLocked(key));
    }

    @Test
    void testConcurrentLocks() throws InterruptedException {
        String key = "concurrent-lock";
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger acquired = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            String id = "inst-" + i;
            executor.submit(() -> {
                try {
                    if (lock.tryLock(key, id, Duration.ofSeconds(1))) {
                        acquired.incrementAndGet();
                        Thread.sleep(100);
                        lock.unlock(key, id);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        assertEquals(1, acquired.get());
    }

    @Test
    void testOnlyOwnerUnlocks() {
        String key = "owner-lock";
        String owner = "owner";
        String other = "other";

        assertTrue(lock.tryLock(key, owner, Duration.ofSeconds(5)));
        assertFalse(lock.unlock(key, other));
        assertTrue(lock.isLocked(key));
        assertTrue(lock.unlock(key, owner));
    }
}
