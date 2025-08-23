package com.taskscheduler.service;

import com.taskscheduler.dto.TaskDefinitionCreateRequest;
import com.taskscheduler.dto.TaskDefinitionResponse;
import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.exception.TaskNameAlreadyExistsException;
import com.taskscheduler.exception.TaskNotFoundException;
import com.taskscheduler.repository.TaskDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDefinitionServiceTest {
    
    @Mock
    private TaskDefinitionRepository repository;
    
    @InjectMocks
    private TaskDefinitionService service;
    
    private TaskDefinition testTask;
    private TaskDefinitionCreateRequest createRequest;
    
    @BeforeEach
    void setUp() {
        testTask = new TaskDefinition(
            "Test Task",
            "Test Description", 
            "0 0 12 * * ?",
            TaskDefinition.TaskStatus.ACTIVE
        );
        testTask.setId(1L);
        
        createRequest = new TaskDefinitionCreateRequest(
            "Test Task",
            "Test Description",
            "0 0 12 * * ?",
            TaskDefinition.TaskStatus.ACTIVE,
            "com.example.TestTask",
            "{\"param1\":\"value1\"}"
        );
    }
    
    @Test
    void createTask_ValidRequest_ShouldReturnTaskResponse() {
        when(repository.existsByNameIgnoreCaseAndIdNot(anyString(), isNull())).thenReturn(false);
        when(repository.save(any(TaskDefinition.class))).thenReturn(testTask);
        
        TaskDefinitionResponse response = service.createTask(createRequest);
        
        assertNotNull(response);
        assertEquals("Test Task", response.name());
        verify(repository).save(any(TaskDefinition.class));
    }
    
    @Test
    void createTask_DuplicateName_ShouldThrowException() {
        when(repository.existsByNameIgnoreCaseAndIdNot(anyString(), isNull())).thenReturn(true);
        
        assertThrows(TaskNameAlreadyExistsException.class, 
            () -> service.createTask(createRequest));
    }
    
    @Test
    void getTaskById_ExistingId_ShouldReturnTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(testTask));
        
        TaskDefinitionResponse response = service.getTaskById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test Task", response.name());
    }
    
    @Test
    void getTaskById_NonExistingId_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(TaskNotFoundException.class, 
            () -> service.getTaskById(1L));
    }
}
