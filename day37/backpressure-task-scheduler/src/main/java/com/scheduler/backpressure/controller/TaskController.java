package com.scheduler.backpressure.controller;

import com.scheduler.backpressure.metrics.MetricsService;
import com.scheduler.backpressure.model.Task;
import com.scheduler.backpressure.service.RateLimiterService;
import com.scheduler.backpressure.service.TaskProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskProducerService producerService;
    private final RateLimiterService rateLimiterService;
    private final MetricsService metricsService;
    
    public TaskController(TaskProducerService producerService, 
                         RateLimiterService rateLimiterService,
                         MetricsService metricsService) {
        this.producerService = producerService;
        this.rateLimiterService = rateLimiterService;
        this.metricsService = metricsService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> submitTask(@RequestBody Task task) {
        producerService.sendTask(task);
        Map<String, String> response = new HashMap<>();
        response.put("status", "submitted");
        response.put("taskId", task.getId());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/burst")
    public ResponseEntity<Map<String, Object>> submitBurst(@RequestParam int count) {
        producerService.sendBurst(count);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "burst_submitted");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("processed", metricsService.getTasksProcessedCount());
        metrics.put("throttled", metricsService.getTasksThrottledCount());
        metrics.put("rateLimitEnabled", rateLimiterService.isEnabled());
        metrics.put("maxRatePerSecond", rateLimiterService.getMaxPermitsPerSecond());
        return ResponseEntity.ok(metrics);
    }
    
    @PostMapping("/rate-limiter/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleRateLimiter() {
        boolean newState = !rateLimiterService.isEnabled();
        rateLimiterService.setEnabled(newState);
        Map<String, Boolean> response = new HashMap<>();
        response.put("enabled", newState);
        return ResponseEntity.ok(response);
    }
}
