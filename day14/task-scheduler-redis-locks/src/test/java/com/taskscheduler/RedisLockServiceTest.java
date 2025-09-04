package com.taskscheduler;

import com.taskscheduler.service.RedisLockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class RedisLockServiceTest {
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }
    
    @Autowired
    private RedisLockService lockService;
    
    @Test
    public void testAcquireAndReleaseLock() {
        String taskId = "test-task-1";
        String executorId = "test-executor";
        
        // Acquire lock
        assertTrue(lockService.acquireLock(taskId, executorId));
        
        // Try to acquire same lock with different executor - should fail
        assertFalse(lockService.acquireLock(taskId, "other-executor"));
        
        // Release lock
        assertTrue(lockService.releaseLock(taskId, executorId));
        
        // Now other executor should be able to acquire
        assertTrue(lockService.acquireLock(taskId, "other-executor"));
    }
}
