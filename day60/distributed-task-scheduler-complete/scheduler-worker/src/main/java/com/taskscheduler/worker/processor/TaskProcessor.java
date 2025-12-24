package com.taskscheduler.worker.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskProcessor {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @KafkaListener(topics = "task-executions", groupId = "worker-group")
    public void processTask(String message) {
        try {
            log.info("Processing task: {}", message);
            
            Map<String, Object> taskData = objectMapper.readValue(message, Map.class);
            Long taskId = ((Number) taskData.get("taskId")).longValue();
            Long executionId = ((Number) taskData.get("executionId")).longValue();
            String handler = (String) taskData.get("handler");
            
            // Simulate task execution
            Thread.sleep(2000 + (long)(Math.random() * 3000));
            
            boolean success = Math.random() > 0.1; // 90% success rate
            String result = success ? 
                "Task completed successfully" : 
                "Task failed due to simulated error";
            
            // Report back to core
            String completionUrl = String.format(
                "http://localhost:8083/api/tasks/%d/executions/%d/complete?success=%b&message=%s",
                taskId, executionId, success, result
            );
            
            restTemplate.postForEntity(completionUrl, null, String.class);
            
            log.info("Task {} execution {} {}", taskId, executionId, 
                success ? "succeeded" : "failed");
                
        } catch (Exception e) {
            log.error("Error processing task", e);
        }
    }
}
