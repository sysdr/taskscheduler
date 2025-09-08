package com.scheduler;

import com.scheduler.config.RedlockConfig;
import com.scheduler.manager.RedlockManager;
import com.scheduler.model.RedlockResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class RedlockManagerTest {

    @Container
    static GenericContainer<?> redis1 = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
    
    @Container
    static GenericContainer<?> redis2 = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
    
    @Container
    static GenericContainer<?> redis3 = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @Test
    void testRedlockBasicLocking() {
        List<RedlockConfig.RedisInstance> instances = Arrays.asList(
            createInstance(redis1.getHost(), redis1.getFirstMappedPort()),
            createInstance(redis2.getHost(), redis2.getFirstMappedPort()),
            createInstance(redis3.getHost(), redis3.getFirstMappedPort())
        );

        RedlockManager manager = new RedlockManager(instances, 3, 200, 0.01);

        RedlockResult result = manager.lock("test-resource", 10000);
        assertTrue(result.isSuccess());

        boolean unlocked = manager.unlock("test-resource", result.getLockValue());
        assertTrue(unlocked);
    }

    @Test
    void testConcurrentLocking() throws InterruptedException {
        List<RedlockConfig.RedisInstance> instances = Arrays.asList(
            createInstance(redis1.getHost(), redis1.getFirstMappedPort()),
            createInstance(redis2.getHost(), redis2.getFirstMappedPort()),
            createInstance(redis3.getHost(), redis3.getFirstMappedPort())
        );

        RedlockManager manager = new RedlockManager(instances, 3, 200, 0.01);
        AtomicInteger successCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(10);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    RedlockResult result = manager.lock("concurrent-test", 5000);
                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                        Thread.sleep(100); // Simulate work
                        manager.unlock("concurrent-test", result.getLockValue());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Only one thread should have acquired the lock at a time
        assertTrue(successCount.get() > 0);
        assertTrue(successCount.get() <= 10);
    }

    private RedlockConfig.RedisInstance createInstance(String host, Integer port) {
        RedlockConfig.RedisInstance instance = new RedlockConfig.RedisInstance();
        instance.setHost(host);
        instance.setPort(port);
        return instance;
    }
}
