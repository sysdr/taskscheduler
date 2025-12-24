package com.taskscheduler.api.controller;

import com.taskscheduler.api.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskController {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CORE_URL = "http://localhost:8081";
    
    @PostMapping("/{taskId}/executions/{executionId}/complete")
    public ResponseEntity<Map<String, String>> completeTask(
            @PathVariable Long taskId,
            @PathVariable Long executionId,
            @RequestParam boolean success,
            @RequestParam String message) {
        
        try {
            String url = String.format("%s/tasks/%d/complete?executionId=%d&success=%b&message=%s",
                CORE_URL, taskId, executionId, success, URLEncoder.encode(message, StandardCharsets.UTF_8));
            restTemplate.postForEntity(url, null, String.class);
            
            return ResponseEntity.ok(Map.of("status", "completed"));
        } catch (Exception e) {
            log.error("Error completing task", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", 42);
        stats.put("runningTasks", 5);
        stats.put("completedToday", 127);
        stats.put("failedToday", 3);
        stats.put("avgExecutionTime", 2456);
        stats.put("successRate", 97.6);
        return ResponseEntity.ok(stats);
    }
}
