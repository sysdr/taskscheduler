package com.taskscheduler.consumer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeadLetterHandler {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterHandler.class);
    private static final String DEAD_LETTER_TOPIC = "task-dead-letter";
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DeadLetterHandler(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendToDeadLetterQueue(Task task, ProcessingResult result) {
        try {
            // Create enhanced dead letter record with error details
            Map<String, Object> deadLetterRecord = new HashMap<>();
            deadLetterRecord.put("originalTask", task);
            deadLetterRecord.put("failureReason", result.getMessage());
            deadLetterRecord.put("processingTimeMs", result.getProcessingTimeMs());
            deadLetterRecord.put("failedAt", LocalDateTime.now());
            deadLetterRecord.put("finalRetryCount", task.getRetryCount());
            
            if (result.getError().isPresent()) {
                deadLetterRecord.put("errorType", result.getError().get().getClass().getSimpleName());
                deadLetterRecord.put("errorMessage", result.getError().get().getMessage());
            }

            String deadLetterJson = objectMapper.writeValueAsString(deadLetterRecord);
            
            kafkaTemplate.send(DEAD_LETTER_TOPIC, task.getId(), deadLetterJson)
                .whenComplete((result_future, ex) -> {
                    if (ex == null) {
                        SendResult<String, String> sendResult = result_future;
                        log.info("💀 Task {} sent to dead letter queue at offset {}",
                                task.getId(), sendResult.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send task {} to dead letter queue: {}", 
                                task.getId(), ex.getMessage());
                    }
                });
                
        } catch (Exception e) {
            log.error("Error creating dead letter record for task {}: {}", task.getId(), e.getMessage());
        }
    }
}
