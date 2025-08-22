package com.taskscheduler.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskDefinition model
 */
class TaskDefinitionTest {
    
    private Validator validator;
    private TaskDefinition taskDefinition;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        taskDefinition = new TaskDefinition();
        taskDefinition.setName("Test Task");
        taskDefinition.setTaskType("EMAIL_TASK");
        taskDefinition.setCronExpression("0 0 9 * * ?");
        taskDefinition.setNextRunTime(LocalDateTime.now().plusHours(1));
    }
    
    @Test
    void testValidTaskDefinition() {
        Set<ConstraintViolation<TaskDefinition>> violations = validator.validate(taskDefinition);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testTaskDefinitionWithBlankName() {
        taskDefinition.setName("");
        Set<ConstraintViolation<TaskDefinition>> violations = validator.validate(taskDefinition);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Task name is required")));
    }
    
    @Test
    void testTaskDefinitionWithInvalidPriority() {
        taskDefinition.setPriority(11);
        Set<ConstraintViolation<TaskDefinition>> violations = validator.validate(taskDefinition);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Priority must be between 1 and 10")));
    }
    
    @Test
    void testTaskDefinitionWithNegativeTimeout() {
        taskDefinition.setTimeoutSeconds(0);
        Set<ConstraintViolation<TaskDefinition>> violations = validator.validate(taskDefinition);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Timeout must be at least 1 second")));
    }
    
    @Test
    void testIsEligibleToRun() {
        taskDefinition.setStatus(TaskStatus.ACTIVE);
        taskDefinition.setNextRunTime(LocalDateTime.now().minusMinutes(1));
        assertTrue(taskDefinition.isEligibleToRun());
        
        taskDefinition.setStatus(TaskStatus.PAUSED);
        assertFalse(taskDefinition.isEligibleToRun());
        
        taskDefinition.setStatus(TaskStatus.ACTIVE);
        taskDefinition.setNextRunTime(LocalDateTime.now().plusMinutes(1));
        assertFalse(taskDefinition.isEligibleToRun());
    }
    
    @Test
    void testMarkAsCompleted() {
        taskDefinition.markAsCompleted();
        assertEquals(TaskStatus.COMPLETED, taskDefinition.getStatus());
        assertNotNull(taskDefinition.getUpdatedAt());
    }
    
    @Test
    void testMarkAsFailed() {
        taskDefinition.markAsFailed();
        assertEquals(TaskStatus.FAILED, taskDefinition.getStatus());
        assertNotNull(taskDefinition.getUpdatedAt());
    }
    
    @Test
    void testPauseAndResume() {
        taskDefinition.pause();
        assertEquals(TaskStatus.PAUSED, taskDefinition.getStatus());
        
        taskDefinition.resume();
        assertEquals(TaskStatus.ACTIVE, taskDefinition.getStatus());
    }
    
    @Test
    void testEqualsAndHashCode() {
        TaskDefinition task1 = new TaskDefinition();
        TaskDefinition task2 = new TaskDefinition();
        
        assertNotEquals(task1, task2); // Different IDs
        assertNotEquals(task1.hashCode(), task2.hashCode());
        
        task2.setId(task1.getId());
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }
    
    @Test
    void testToString() {
        String toString = taskDefinition.toString();
        assertTrue(toString.contains("TaskDefinition"));
        assertTrue(toString.contains(taskDefinition.getName()));
        assertTrue(toString.contains(taskDefinition.getTaskType()));
    }
}
