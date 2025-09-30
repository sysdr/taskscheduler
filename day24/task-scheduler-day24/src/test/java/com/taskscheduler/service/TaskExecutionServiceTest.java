package com.taskscheduler.service;

import com.taskscheduler.config.RetryPolicy;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecutionResult;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskExecutionServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private RetryPolicy retryPolicy;
    
    private TaskExecutionService taskExecutionService;
    
    @BeforeEach
    void setUp() {
        taskExecutionService = new TaskExecutionService(taskRepository, retryPolicy);
    }
    
    @Test
    void testExecuteTask_Success() {
        // Given
        Task task = new Task("Test Task", "generic");
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        // When
        TaskExecutionResult result = taskExecutionService.executeTask(task);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getCompletedTime());
        verify(taskRepository, atLeast(2)).save(task);
    }
    
    @Test
    void testRetryPolicyCalculation() {
        // Given
        RetryPolicy policy = new RetryPolicy();
        
        // When & Then
        long delay0 = policy.calculateBackoffDelay(0);
        long delay1 = policy.calculateBackoffDelay(1);
        long delay2 = policy.calculateBackoffDelay(2);
        
        assertTrue(delay0 >= 1000 && delay0 <= 1100); // Base + jitter
        assertTrue(delay1 >= 2000 && delay1 <= 2200); // 2x base + jitter
        assertTrue(delay2 >= 4000 && delay2 <= 4400); // 4x base + jitter
    }
}
