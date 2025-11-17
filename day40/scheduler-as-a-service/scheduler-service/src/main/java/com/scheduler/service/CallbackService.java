package com.scheduler.service;

import com.scheduler.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class CallbackService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public void sendCallback(Task task) {
        try {
            Map<String, Object> callback = new HashMap<>();
            callback.put("taskId", task.getTaskId());
            callback.put("status", task.getStatus().toString());
            callback.put("result", task.getResult());
            callback.put("errorMessage", task.getErrorMessage());
            callback.put("completedAt", task.getCompletedAt().toString());
            
            restTemplate.postForEntity(task.getCallbackUrl(), callback, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send callback: " + e.getMessage());
        }
    }
}
