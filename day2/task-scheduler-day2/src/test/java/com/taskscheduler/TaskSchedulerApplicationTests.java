package com.taskscheduler;

import com.taskscheduler.service.TaskSchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.scheduled.enabled=false"})
class TaskSchedulerApplicationTests {

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Test
    void contextLoads() {
        assertNotNull(taskSchedulerService);
    }

    @Test
    void serviceBeansExist() {
        assertNotNull(taskSchedulerService);
        assertNotNull(taskSchedulerService.getExecutionHistory());
    }
}
