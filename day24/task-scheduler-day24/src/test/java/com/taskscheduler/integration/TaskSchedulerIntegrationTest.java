package com.taskscheduler.integration;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskPriority;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskSchedulerIntegrationTest {
    
    @Autowired
    private TaskService taskService;
    
    @Test
    void testCreateAndRetrieveTask() {
        // Given
        String taskName = "Integration Test Task";
        String taskType = "test";
        
        // When
        Task createdTask = taskService.createTask(
            taskName, taskType, "Test description", 
            TaskPriority.HIGH, null, null, 5
        );
        
        // Then
        assertNotNull(createdTask.getId());
        assertEquals(taskName, createdTask.getName());
        assertEquals(taskType, createdTask.getTaskType());
        assertEquals(TaskStatus.PENDING, createdTask.getStatus());
        assertEquals(TaskPriority.HIGH, createdTask.getPriority());
        assertEquals(5, createdTask.getMaxRetries());
        
        // Verify retrieval
        Task retrievedTask = taskService.getTaskById(createdTask.getId()).orElse(null);
        assertNotNull(retrievedTask);
        assertEquals(createdTask.getId(), retrievedTask.getId());
    }
}
