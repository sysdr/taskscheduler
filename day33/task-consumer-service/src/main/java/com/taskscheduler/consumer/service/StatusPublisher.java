package com.taskscheduler.consumer.service;

import com.taskscheduler.consumer.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatusPublisher {
    private static final Logger logger = LoggerFactory.getLogger(StatusPublisher.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishStatus(String taskId, String status, String workerId, String errorMessage) {
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("taskId", taskId);
        statusUpdate.put("status", status);
        statusUpdate.put("workerId", workerId);
        statusUpdate.put("timestamp", LocalDateTime.now().toString());
        
        if (errorMessage != null) {
            statusUpdate.put("errorMessage", errorMessage);
        }
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitConfig.TASK_STATUS_EXCHANGE,
                RabbitConfig.TASK_STATUS_ROUTING_KEY,
                statusUpdate
            );
            logger.debug("Published status update for task {}: {}", taskId, status);
        } catch (Exception e) {
            logger.error("Failed to publish status update for task {}", taskId, e);
        }
    }
}
