package com.scheduler.service;

import com.scheduler.model.TaskRequest;
import com.scheduler.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TimeoutTaskServiceTest {

    @Autowired
    private TimeoutTaskService taskService;

    @Test
    void testFastTaskCompletion() throws InterruptedException {
        TaskRequest request = new TaskRequest(
            "test-fast-task",
            "FAST_TASK",
            Duration.ofSeconds(5),
            "Test payload"
        );

        String taskId = taskService.submitTask(request);
        assertNotNull(taskId);

        // Wait for completion
        Thread.sleep(2000);

        var execution = taskService.getTask(taskId);
        assertNotNull(execution);
        assertEquals(TaskStatus.COMPLETED, execution.getStatus());
    }

    @Test
    void testTaskTimeout() throws InterruptedException {
        TaskRequest request = new TaskRequest(
            "test-timeout-task",
            "INFINITE_TASK", // Infinite task that should timeout
            Duration.ofSeconds(1), // 1 second timeout
            "Timeout test payload"
        );

        String taskId = taskService.submitTask(request);
        assertNotNull(taskId);

        // Wait for timeout processing to complete
        Thread.sleep(3000);

        var execution = taskService.getTask(taskId);
        assertNotNull(execution);
        assertEquals(TaskStatus.COMPLETED, execution.getStatus());
        assertTrue(execution.getResult().contains("TIMEOUT"));
    }
}
