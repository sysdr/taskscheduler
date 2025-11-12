package com.scheduler.backpressure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.backpressure.model.Task;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "task-queue";
    
    public TaskProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }
    
    public void sendTask(Task task) {
        try {
            String taskJson = objectMapper.writeValueAsString(task);
            kafkaTemplate.send(TOPIC, task.getId(), taskJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize task", e);
        }
    }
    
    public void sendBurst(int count) {
        for (int i = 0; i < count; i++) {
            Task task = new Task("email", "Burst task " + i);
            sendTask(task);
        }
    }
}
