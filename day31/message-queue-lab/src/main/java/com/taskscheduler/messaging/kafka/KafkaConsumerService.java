package com.taskscheduler.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.messaging.model.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "task-submission", groupId = "task-scheduler-group")
    public void consumeTaskSubmission(String message) {
        try {
            TaskMessage taskMessage = objectMapper.readValue(message, TaskMessage.class);
            logger.info("📥 Received task submission: {}", taskMessage);
            
            // Simulate task processing
            Thread.sleep(100);
            logger.info("✅ Processed task submission: {}", taskMessage.getTaskId());
            
        } catch (Exception e) {
            logger.error("❌ Error processing task submission: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "task-execution", groupId = "task-scheduler-group")
    public void consumeTaskExecution(String message) {
        try {
            TaskMessage taskMessage = objectMapper.readValue(message, TaskMessage.class);
            logger.info("⚡ Received task execution: {}", taskMessage);
            
            // Simulate task execution
            Thread.sleep(200);
            logger.info("✅ Executed task: {}", taskMessage.getTaskId());
            
        } catch (Exception e) {
            logger.error("❌ Error executing task: {}", e.getMessage());
        }
    }
}
