package com.scheduler.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class ImageProcessorHandler {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> handleRequest(Map<String, Object> input) {
        try {
            Long taskId = ((Number) input.get("taskId")).longValue();
            String taskType = (String) input.get("taskType");
            
            // Simulate image processing
            Thread.sleep((long) (Math.random() * 2000 + 1000));
            
            return Map.of(
                "success", true,
                "taskId", taskId,
                "result", "Image processed successfully",
                "processedAt", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
}
