package com.taskscheduler;

import com.taskscheduler.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskStatusTest {
    
    @Test
    void testValidTransitions() {
        // PENDING can transition to RUNNING
        assertTrue(TaskStatus.PENDING.canTransitionTo(TaskStatus.RUNNING));
        
        // RUNNING can transition to SUCCEEDED or FAILED
        assertTrue(TaskStatus.RUNNING.canTransitionTo(TaskStatus.SUCCEEDED));
        assertTrue(TaskStatus.RUNNING.canTransitionTo(TaskStatus.FAILED));
        
        // Terminal states cannot transition
        assertFalse(TaskStatus.SUCCEEDED.canTransitionTo(TaskStatus.RUNNING));
        assertFalse(TaskStatus.FAILED.canTransitionTo(TaskStatus.RUNNING));
    }
    
    @Test
    void testInvalidTransitions() {
        // PENDING cannot transition to SUCCEEDED or FAILED directly
        assertFalse(TaskStatus.PENDING.canTransitionTo(TaskStatus.SUCCEEDED));
        assertFalse(TaskStatus.PENDING.canTransitionTo(TaskStatus.FAILED));
        
        // No self-transitions allowed
        assertFalse(TaskStatus.PENDING.canTransitionTo(TaskStatus.PENDING));
        assertFalse(TaskStatus.RUNNING.canTransitionTo(TaskStatus.RUNNING));
    }
    
    @Test
    void testStatusProperties() {
        // Terminal states
        assertTrue(TaskStatus.SUCCEEDED.isTerminal());
        assertTrue(TaskStatus.FAILED.isTerminal());
        assertFalse(TaskStatus.PENDING.isTerminal());
        assertFalse(TaskStatus.RUNNING.isTerminal());
        
        // Active states
        assertTrue(TaskStatus.RUNNING.isActive());
        assertFalse(TaskStatus.PENDING.isActive());
        assertFalse(TaskStatus.SUCCEEDED.isActive());
        assertFalse(TaskStatus.FAILED.isActive());
        
        // Completed states
        assertTrue(TaskStatus.SUCCEEDED.isCompleted());
        assertTrue(TaskStatus.FAILED.isCompleted());
        assertFalse(TaskStatus.PENDING.isCompleted());
        assertFalse(TaskStatus.RUNNING.isCompleted());
    }
}
