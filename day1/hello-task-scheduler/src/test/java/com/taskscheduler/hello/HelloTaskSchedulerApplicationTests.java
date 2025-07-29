package com.taskscheduler.hello;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic test to ensure the application context loads successfully
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.task.scheduling.pool.size=1"
})
class HelloTaskSchedulerApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context
        // can be loaded successfully with all our configurations
    }
}
