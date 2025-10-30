package com.taskscheduler.messaging.rabbitmq;

import com.taskscheduler.messaging.model.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumerService.class);

    @RabbitListener(queues = "task.submission.queue")
    public void consumeTaskSubmission(TaskMessage taskMessage) {
        try {
            logger.info("📥 Received RabbitMQ task submission: {}", taskMessage);
            
            // Simulate task processing
            Thread.sleep(100);
            logger.info("✅ Processed RabbitMQ task submission: {}", taskMessage.getTaskId());
            
        } catch (Exception e) {
            logger.error("❌ Error processing RabbitMQ task submission: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "task.execution.queue")
    public void consumeTaskExecution(TaskMessage taskMessage) {
        try {
            logger.info("⚡ Received RabbitMQ task execution: {}", taskMessage);
            
            // Simulate task execution
            Thread.sleep(200);
            logger.info("✅ Executed RabbitMQ task: {}", taskMessage.getTaskId());
            
        } catch (Exception e) {
            logger.error("❌ Error executing RabbitMQ task: {}", e.getMessage());
        }
    }
}
