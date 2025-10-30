package com.taskscheduler.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.messaging.model.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    public void sendTaskMessage(String topic, TaskMessage taskMessage) {
        try {
            String messageJson = objectMapper.writeValueAsString(taskMessage);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(topic, taskMessage.getTaskId(), messageJson);
            
            future.thenAccept(result -> {
                logger.info("📤 Sent message to Kafka topic '{}': {}", topic, taskMessage);
            }).exceptionally(ex -> {
                logger.error("❌ Failed to send message to Kafka topic '{}': {}", topic, ex.getMessage());
                return null;
            });
            
        } catch (JsonProcessingException e) {
            logger.error("❌ Error serializing task message: {}", e.getMessage());
        }
    }
}
