package com.taskscheduler.consumer;

import com.taskscheduler.consumer.model.TaskExecutionRequest;
import com.taskscheduler.consumer.service.TaskProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672"
})
class TaskConsumerApplicationTests {

    @MockBean
    private TaskProcessor taskProcessor;

    @Test
    void contextLoads() {
        // Test that Spring context loads successfully
    }
    
    @Test
    void testTaskExecutionRequest() {
        TaskExecutionRequest request = new TaskExecutionRequest();
        request.setTaskId("test-123");
        request.setTaskType("email");
        request.setPayload("{\"to\":\"test@example.com\"}");
        
        assert request.getTaskId().equals("test-123");
        assert request.getTaskType().equals("email");
    }
}
