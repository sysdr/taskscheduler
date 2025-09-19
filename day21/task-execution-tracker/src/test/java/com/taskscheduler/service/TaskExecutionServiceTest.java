package com.taskscheduler.service;

import com.taskscheduler.entity.ExecutionStatus;
import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.repository.TaskExecutionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TaskExecutionServiceTest {
    
    @Autowired
    private TaskExecutionService executionService;
    
    @Autowired
    private TaskExecutionRepository repository;
    
    @Test
    void testCreateExecution() {
        TaskExecution execution = executionService.createExecution("TestTask", "Test Description");
        
        assertNotNull(execution.getId());
        assertNotNull(execution.getExecutionId());
        assertEquals("TestTask", execution.getTaskName());
        assertEquals("Test Description", execution.getTaskDescription());
        assertEquals(ExecutionStatus.PENDING, execution.getStatus());
    }
    
    @Test
    void testExecutionLifecycle() {
        // Create execution
        TaskExecution execution = executionService.createExecution("LifecycleTask", "Test lifecycle");
        String executionId = execution.getExecutionId();
        
        // Start execution
        TaskExecution started = executionService.startExecution(executionId);
        assertEquals(ExecutionStatus.RUNNING, started.getStatus());
        assertNotNull(started.getStartTime());
        
        // Complete execution
        TaskExecution completed = executionService.completeExecution(executionId);
        assertEquals(ExecutionStatus.SUCCESS, completed.getStatus());
        assertNotNull(completed.getEndTime());
        assertNotNull(completed.getDurationMs());
    }
    
    @Test
    void testFailExecution() {
        TaskExecution execution = executionService.createExecution("FailTask", "Test failure");
        executionService.startExecution(execution.getExecutionId());
        
        TaskExecution failed = executionService.failExecution(execution.getExecutionId(), 
                "Test error", "Test stack trace");
        
        assertEquals(ExecutionStatus.FAILED, failed.getStatus());
        assertEquals("Test error", failed.getErrorMessage());
        assertEquals("Test stack trace", failed.getStackTrace());
    }
}
