package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskQueueService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskQueueService.class);
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${app.kafka.task-execution-topic}")
    private String taskExecutionTopic;

    public CompletableFuture<SendResult<String, Object>> sendTaskToQueue(Task task) {
        logger.info("Sending task to queue: {} - {}", task.getTaskId(), task.getTaskName());
        
        return kafkaTemplate.send(taskExecutionTopic, task.getTaskId(), task)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Task successfully sent to queue: {} with offset: {}", 
                        task.getTaskId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send task to queue: {}", task.getTaskId(), ex);
                }
            });
    }

    public void sendTaskToDeadLetterQueue(Task task, String errorMessage) {
        logger.warn("Sending task to dead letter queue: {} - Error: {}", task.getTaskId(), errorMessage);
        
        kafkaTemplate.send(taskExecutionTopic + "-dlq", task.getTaskId(), task)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Task sent to DLQ: {}", task.getTaskId());
                } else {
                    logger.error("Failed to send task to DLQ: {}", task.getTaskId(), ex);
                }
            });
    }
}
