package com.taskscheduler.messaging;

import com.taskscheduler.messaging.kafka.KafkaProducerService;
import com.taskscheduler.messaging.model.TaskMessage;
import com.taskscheduler.messaging.rabbitmq.RabbitMQProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MessageQueueLabApplicationTests {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @Test
    void contextLoads() {
        // Test that Spring context loads properly
    }

    @Test
    void testKafkaProducerService() {
        TaskMessage testMessage = new TaskMessage("TEST_TASK", "Test payload", 1);
        // Note: This test requires Kafka to be running
        // kafkaProducerService.sendTaskMessage("test-topic", testMessage);
    }

    @Test
    void testRabbitMQProducerService() {
        TaskMessage testMessage = new TaskMessage("TEST_TASK", "Test payload", 1);
        // Note: This test requires RabbitMQ to be running
        // rabbitMQProducerService.sendTaskMessage("test.routing.key", testMessage);
    }
}
