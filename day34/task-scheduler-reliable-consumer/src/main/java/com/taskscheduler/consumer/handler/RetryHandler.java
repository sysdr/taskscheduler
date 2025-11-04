package com.taskscheduler.consumer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.consumer.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class RetryHandler {
    private static final Logger log = LoggerFactory.getLogger(RetryHandler.class);
    private static final String RETRY_TOPIC = "task-retry";
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RetryHandler(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Async
    public CompletableFuture<Void> scheduleRetry(Task task, long delayMs) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Wait for the delay period
                Thread.sleep(delayMs);
                
                // Send task back to retry topic
                String taskJson = objectMapper.writeValueAsString(task);
                kafkaTemplate.send(RETRY_TOPIC, task.getId(), taskJson);
                
                log.info("🔄 Task {} sent to retry topic after {}ms delay", task.getId(), delayMs);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Retry scheduling interrupted for task {}", task.getId());
            } catch (Exception e) {
                log.error("Failed to schedule retry for task {}: {}", task.getId(), e.getMessage());
            }
        });
    }
}
