package com.taskscheduler.service;

import com.taskscheduler.dto.TaskDto;
import com.taskscheduler.entity.Task;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.exception.TaskExecutionException;
import com.taskscheduler.exception.TaskNotFoundException;
import com.taskscheduler.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @InjectMocks
    private TaskService taskService;
    
    @Test
    void createTask_Success() {
        // Given
        TaskDto taskDto = new TaskDto("Test Task", "Test Description");
        Task savedTask = new Task("Test Task", "Test Description");
        savedTask.setId(1L);
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        
        // When
        Task result = taskService.createTask(taskDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void getTaskById_Success() {
        // Given
        Long taskId = 1L;
        Task task = new Task("Test Task", "Test Description");
        task.setId(taskId);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        // When
        Task result = taskService.getTaskById(taskId);
        
        // Then
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getName());
    }
    
    @Test
    void getTaskById_NotFound() {
        // Given
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));
    }
    
    @Test
    void executeTaskWithErrorHandling_TerminalState() {
        // Given
        Long taskId = 1L;
        Task task = new Task("Test Task", "Test Description");
        task.setId(taskId);
        task.setStatus(TaskStatus.SUCCEEDED);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        // When & Then
        assertThrows(TaskExecutionException.class, 
                    () -> taskService.executeTaskWithErrorHandling(taskId));
    }
}
