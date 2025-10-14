package com.scheduler;

import com.scheduler.service.TaskService;
import com.scheduler.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskSchedulerApplicationTests {

    @Autowired
    private TaskService taskService;

    @Test
    void contextLoads() {
        assertNotNull(taskService);
    }

    @Test
    void testTaskCreation() {
        Task task = taskService.createTask("Test Task", "GENERAL");
        assertNotNull(task);
        assertNotNull(task.getId());
        assertEquals("Test Task", task.getName());
        assertEquals("GENERAL", task.getType());
    }
}
