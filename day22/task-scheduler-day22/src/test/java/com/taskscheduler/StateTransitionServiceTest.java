package com.taskscheduler;

import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.repository.TaskExecutionRepository;
import com.taskscheduler.service.StateTransitionService;
import com.taskscheduler.service.TaskMetricsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StateTransitionServiceTest {
    
    @Autowired
    private StateTransitionService stateTransitionService;
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @Test
    void testCreateAndStartTask() {
        // Create task
        TaskExecution task = stateTransitionService.createTask("test-task", "Test details");
        assertNotNull(task.getId());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        
        // Start task
        TaskExecution startedTask = stateTransitionService.startTask(task.getId());
        assertEquals(TaskStatus.RUNNING, startedTask.getStatus());
        assertNotNull(startedTask.getStartedAt());
    }
    
    @Test
    void testCompleteTask() {
        // Create and start task
        TaskExecution task = stateTransitionService.createTask("test-task", "Test details");
        TaskExecution startedTask = stateTransitionService.startTask(task.getId());
        
        // Complete task
        TaskExecution completedTask = stateTransitionService.completeTask(startedTask.getId());
        assertEquals(TaskStatus.SUCCEEDED, completedTask.getStatus());
        assertNotNull(completedTask.getCompletedAt());
        assertNotNull(completedTask.getDurationMs());
    }
    
    @Test
    void testFailTask() {
        // Create and start task
        TaskExecution task = stateTransitionService.createTask("test-task", "Test details");
        TaskExecution startedTask = stateTransitionService.startTask(task.getId());
        
        // Fail task
        String errorMessage = "Test error";
        TaskExecution failedTask = stateTransitionService.failTask(startedTask.getId(), errorMessage);
        assertEquals(TaskStatus.FAILED, failedTask.getStatus());
        assertEquals(errorMessage, failedTask.getErrorMessage());
        assertNotNull(failedTask.getCompletedAt());
    }
    
    @Test
    void testInvalidTransition() {
        // Create task in PENDING state
        TaskExecution task = stateTransitionService.createTask("test-task", "Test details");
        
        // Try to complete directly from PENDING (should fail)
        assertThrows(IllegalStateException.class, () -> {
            stateTransitionService.completeTask(task.getId());
        });
    }
    
    @Test
    void testTransitionValidation() {
        TaskExecution task = stateTransitionService.createTask("test-task", "Test details");
        
        // Valid transition: PENDING -> RUNNING
        assertTrue(stateTransitionService.isValidTransition(task.getId(), TaskStatus.RUNNING));
        
        // Invalid transition: PENDING -> SUCCEEDED
        assertFalse(stateTransitionService.isValidTransition(task.getId(), TaskStatus.SUCCEEDED));
    }
}
