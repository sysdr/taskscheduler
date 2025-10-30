package com.taskscheduler.messaging.rabbitmq;

import com.taskscheduler.messaging.model.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducerService.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendTaskMessage(String routingKey, TaskMessage taskMessage) {
        try {
            rabbitTemplate.convertAndSend("task.exchange", routingKey, taskMessage);
            logger.info("📤 Sent message to RabbitMQ routing key '{}': {}", routingKey, taskMessage);
        } catch (Exception e) {
            logger.error("❌ Failed to send message to RabbitMQ: {}", e.getMessage());
        }
    }
}
