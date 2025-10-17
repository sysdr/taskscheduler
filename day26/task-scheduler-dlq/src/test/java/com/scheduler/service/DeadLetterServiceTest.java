package com.scheduler.service;

import com.scheduler.model.DeadLetterTask;
import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.DeadLetterTaskRepository;
import com.scheduler.repository.TaskRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeadLetterServiceTest {

    @Mock
    private DeadLetterTaskRepository deadLetterRepository;
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private MeterRegistry meterRegistry;
    
    @Mock
    private TaskService taskService;
    
    @InjectMocks
    private DeadLetterService deadLetterService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task("test-task", "{\"test\":\"payload\"}");
        testTask.setId("task-123");
        testTask.setMaxRetries(3);
        testTask.setRetryCount(3);
    }

    @Test
    void testMoveToDeadLetter() {
        // Given
        Exception finalException = new RuntimeException("Task failed after all retries");
        when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(mock(io.micrometer.core.instrument.Counter.class));
        
        // When
        deadLetterService.moveToDeadLetter(testTask, finalException);
        
        // Then
        verify(deadLetterRepository).save(any(DeadLetterTask.class));
        verify(taskRepository).save(testTask);
        assertEquals(TaskStatus.DEAD_LETTER, testTask.getStatus());
        assertEquals(finalException.getMessage(), testTask.getLastError());
    }

    @Test
    void testGetUnprocessedTaskCount() {
        // Given
        when(deadLetterRepository.countUnprocessedTasks()).thenReturn(5L);
        
        // When
        long count = deadLetterService.getUnprocessedTaskCount();
        
        // Then
        assertEquals(5L, count);
        verify(deadLetterRepository).countUnprocessedTasks();
    }
}
