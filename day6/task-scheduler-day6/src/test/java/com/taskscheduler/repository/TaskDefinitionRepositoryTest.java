package com.taskscheduler.repository;

import com.taskscheduler.model.TaskDefinition;
import com.taskscheduler.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TaskDefinitionRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class TaskDefinitionRepositoryTest {
    
    @Autowired
    private TaskDefinitionRepository repository;
    
    @Test
    void testSaveAndFindById() {
        TaskDefinition task = new TaskDefinition("Test Task", "EMAIL_TASK", "0 0 9 * * ?");
        task.setNextRunTime(LocalDateTime.now());
        
        TaskDefinition saved = repository.save(task);
        assertNotNull(saved.getId());
        
        TaskDefinition found = repository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Test Task", found.getName());
    }
    
    @Test
    void testFindByStatus() {
        TaskDefinition activeTask = new TaskDefinition("Active Task", "REPORT_TASK", "0 0 9 * * ?");
        activeTask.setStatus(TaskStatus.ACTIVE);
        activeTask.setNextRunTime(LocalDateTime.now());
        
        TaskDefinition pausedTask = new TaskDefinition("Paused Task", "CLEANUP_TASK", "0 0 9 * * ?");
        pausedTask.setStatus(TaskStatus.PAUSED);
        pausedTask.setNextRunTime(LocalDateTime.now());
        
        repository.save(activeTask);
        repository.save(pausedTask);
        
        List<TaskDefinition> activeTasks = repository.findByStatus(TaskStatus.ACTIVE);
        List<TaskDefinition> pausedTasks = repository.findByStatus(TaskStatus.PAUSED);
        
        assertEquals(1, activeTasks.size());
        assertEquals(1, pausedTasks.size());
        assertEquals("Active Task", activeTasks.get(0).getName());
        assertEquals("Paused Task", pausedTasks.get(0).getName());
    }
    
    @Test
    void testFindEligibleTasks() {
        // Create eligible task (active and due)
        TaskDefinition eligibleTask = new TaskDefinition("Eligible Task", "EMAIL_TASK", "0 0 9 * * ?");
        eligibleTask.setStatus(TaskStatus.ACTIVE);
        eligibleTask.setNextRunTime(LocalDateTime.now().minusMinutes(1));
        eligibleTask.setPriority(8);
        
        // Create not eligible task (future run time)
        TaskDefinition futureTask = new TaskDefinition("Future Task", "REPORT_TASK", "0 0 9 * * ?");
        futureTask.setStatus(TaskStatus.ACTIVE);
        futureTask.setNextRunTime(LocalDateTime.now().plusMinutes(10));
        futureTask.setPriority(5);
        
        repository.save(eligibleTask);
        repository.save(futureTask);
        
        List<TaskDefinition> eligibleTasks = repository.findEligibleTasks(TaskStatus.ACTIVE, LocalDateTime.now());
        
        assertEquals(1, eligibleTasks.size());
        assertEquals("Eligible Task", eligibleTasks.get(0).getName());
    }
    
    @Test
    void testFindByPriorityBetween() {
        TaskDefinition lowPriority = new TaskDefinition("Low Priority", "EMAIL_TASK", "0 0 9 * * ?");
        lowPriority.setPriority(2);
        lowPriority.setNextRunTime(LocalDateTime.now());
        
        TaskDefinition highPriority = new TaskDefinition("High Priority", "REPORT_TASK", "0 0 9 * * ?");
        highPriority.setPriority(9);
        highPriority.setNextRunTime(LocalDateTime.now());
        
        repository.save(lowPriority);
        repository.save(highPriority);
        
        List<TaskDefinition> highPriorityTasks = repository.findByPriorityBetween(8, 10);
        
        assertEquals(1, highPriorityTasks.size());
        assertEquals("High Priority", highPriorityTasks.get(0).getName());
    }
    
    @Test
    void testCountByStatus() {
        TaskDefinition task1 = new TaskDefinition("Task 1", "EMAIL_TASK", "0 0 9 * * ?");
        task1.setStatus(TaskStatus.ACTIVE);
        task1.setNextRunTime(LocalDateTime.now());
        
        TaskDefinition task2 = new TaskDefinition("Task 2", "REPORT_TASK", "0 0 9 * * ?");
        task2.setStatus(TaskStatus.ACTIVE);
        task2.setNextRunTime(LocalDateTime.now());
        
        TaskDefinition task3 = new TaskDefinition("Task 3", "CLEANUP_TASK", "0 0 9 * * ?");
        task3.setStatus(TaskStatus.FAILED);
        task3.setNextRunTime(LocalDateTime.now());
        
        repository.save(task1);
        repository.save(task2);
        repository.save(task3);
        
        long activeCount = repository.countByStatus(TaskStatus.ACTIVE);
        long failedCount = repository.countByStatus(TaskStatus.FAILED);
        
        assertEquals(2, activeCount);
        assertEquals(1, failedCount);
    }
    
    @Test
    void testFindByNameIgnoreCase() {
        TaskDefinition task = new TaskDefinition("Test Task Name", "EMAIL_TASK", "0 0 9 * * ?");
        task.setNextRunTime(LocalDateTime.now());
        repository.save(task);
        
        assertTrue(repository.findByNameIgnoreCase("test task name").isPresent());
        assertTrue(repository.findByNameIgnoreCase("TEST TASK NAME").isPresent());
        assertFalse(repository.findByNameIgnoreCase("nonexistent").isPresent());
    }
}
