package com.taskscheduler;

import com.taskscheduler.component.IdempotentTaskWrapper;
import com.taskscheduler.model.ExecutionStatus;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.repository.TaskExecutionRepository;
import com.taskscheduler.service.ExecutionTracker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class IdempotencyTest {

    @Autowired
    private IdempotentTaskWrapper taskWrapper;

    @Autowired
    private ExecutionTracker executionTracker;

    @Autowired
    private TaskExecutionRepository repository;

    @Test
    public void testIdempotentExecution() {
        AtomicInteger executionCount = new AtomicInteger(0);
        
        String result1 = taskWrapper.executeIdempotent("test_task", () -> {
            executionCount.incrementAndGet();
            return "Success";
        }, "param1", "param2");

        String result2 = taskWrapper.executeIdempotent("test_task", () -> {
            executionCount.incrementAndGet();
            return "Success";
        }, "param1", "param2");

        assertEquals("Success", result1);
        assertEquals("Success", result2);
        assertEquals(1, executionCount.get(), "Task should only execute once");
    }

    @Test
    public void testConcurrentIdempotentExecution() throws InterruptedException {
        AtomicInteger executionCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(5);
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    taskWrapper.executeIdempotent("concurrent_test", () -> {
                        executionCount.incrementAndGet();
                        try { Thread.sleep(100); } catch (InterruptedException e) {}
                        return "Concurrent Success";
                    }, "same_param");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(executionCount.get() <= 1, "Task should execute at most once even with concurrent access");
    }

    @Test
    public void testExecutionIdGeneration() {
        String id1 = executionTracker.generateExecutionId("task1", "param1", "param2");
        String id2 = executionTracker.generateExecutionId("task1", "param1", "param2");
        String id3 = executionTracker.generateExecutionId("task1", "param1", "param3");

        assertEquals(id1, id2, "Same parameters should generate same execution ID");
        assertNotEquals(id1, id3, "Different parameters should generate different execution IDs");
    }

    @Test
    public void testExecutionTracking() {
        String executionId = executionTracker.generateExecutionId("tracking_test", "param");
        
        TaskExecution execution = executionTracker.startExecution(executionId, "tracking_test", "param");
        assertNotNull(execution);
        assertEquals(ExecutionStatus.RUNNING, execution.getStatus());

        executionTracker.recordSuccess(executionId, "Test Success");
        
        TaskExecution completed = repository.findByExecutionId(executionId).orElse(null);
        assertNotNull(completed);
        assertEquals(ExecutionStatus.COMPLETED, completed.getStatus());
        assertEquals("Test Success", completed.getResult());
    }
}
