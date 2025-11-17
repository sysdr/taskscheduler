package com.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentSchedulerClient {
    
    @Value("${scheduler.url:http://localhost:8080}")
    private String schedulerUrl;
    
    @Value("${scheduler.api-key}")
    private String apiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private String cachedToken;
    
    public Map<String, Object> submitTask(String taskName, String payload, String callbackUrl) {
        String token = getToken();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        Map<String, Object> request = new HashMap<>();
        request.put("taskName", taskName);
        request.put("payload", payload);
        request.put("callbackUrl", callbackUrl);
        request.put("scheduledFor", LocalDateTime.now().plusSeconds(30).toString());
        request.put("priority", 8);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            schedulerUrl + "/api/v1/tasks",
            HttpMethod.POST,
            entity,
            Map.class
        );
        
        return response.getBody();
    }
    
    private String getToken() {
        if (cachedToken != null) {
            return cachedToken;
        }
        
        Map<String, String> request = new HashMap<>();
        request.put("apiKey", apiKey);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
            schedulerUrl + "/api/v1/auth/token",
            request,
            Map.class
        );
        
        cachedToken = (String) response.getBody().get("token");
        return cachedToken;
    }
}
