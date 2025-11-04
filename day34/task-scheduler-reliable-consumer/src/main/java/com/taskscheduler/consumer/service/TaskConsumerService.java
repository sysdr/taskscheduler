package com.taskscheduler.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.consumer.handler.MessageAcknowledgmentHandler;
import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskConsumerService {
    private static final Logger log = LoggerFactory.getLogger(TaskConsumerService.class);
    
    private final List<TaskProcessor> processors;
    private final ObjectMapper objectMapper;
    private final MessageAcknowledgmentHandler acknowledgmentHandler;

    public TaskConsumerService(List<TaskProcessor> processors, 
                              ObjectMapper objectMapper,
                              MessageAcknowledgmentHandler acknowledgmentHandler) {
        this.processors = processors;
        this.objectMapper = objectMapper;
        this.acknowledgmentHandler = acknowledgmentHandler;
    }

    @KafkaListener(topics = {"task-execution", "task-retry"}, groupId = "task-consumer-group")
    public void consumeTask(@Payload String message,
                           Acknowledgment acknowledgment,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("📥 Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        
        try {
            // Parse the task
            Task task = objectMapper.readValue(message, Task.class);
            log.info("🎯 Processing task: {} of type: {}", task.getId(), task.getType());
            
            // Find appropriate processor
            TaskProcessor processor = processors.stream()
                    .filter(p -> p.canProcess(task.getType()))
                    .findFirst()
                    .orElse(null);
            
            ProcessingResult result;
            if (processor == null) {
                result = ProcessingResult.permanentFailure(
                    "No processor found for task type: " + task.getType(),
                    new IllegalArgumentException("Unsupported task type"),
                    0
                );
            } else {
                result = processor.process(task);
            }
            
            // Handle the result and acknowledgment
            acknowledgmentHandler.handleProcessingResult(task, result, acknowledgment, topic, partition, offset);
            
        } catch (Exception e) {
            log.error("💥 Error processing message: {}", e.getMessage(), e);
            
            // For parsing errors, we acknowledge the message to avoid infinite reprocessing
            acknowledgment.acknowledge();
        }
    }
}
