package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.model.TaskPriority;
import com.scheduler.service.MetricsService;
import com.scheduler.service.TaskPublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TaskPublisherService publisherService;
    private final MetricsService metricsService;
    
    public TaskController(TaskPublisherService publisherService, MetricsService metricsService) {
        this.publisherService = publisherService;
        this.metricsService = metricsService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> submitTask(@RequestBody Task task) {
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.NORMAL);
        }
        
        publisherService.publishTask(task);
        
        Map<String, String> response = new HashMap<>();
        response.put("taskId", task.getId());
        response.put("priority", task.getPriority().name());
        response.put("status", "SUBMITTED");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> submitBulkTasks(
            @RequestParam int count,
            @RequestParam TaskPriority priority) {
        
        for (int i = 0; i < count; i++) {
            Task task = new Task(
                    "Task-" + priority.name() + "-" + i,
                    "Bulk task payload " + i,
                    priority
            );
            publisherService.publishTask(task);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("submitted", count);
        response.put("priority", priority.name());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("highPriorityQueueDepth", metricsService.getQueueDepth("high"));
        metrics.put("normalPriorityQueueDepth", metricsService.getQueueDepth("normal"));
        metrics.put("lowPriorityQueueDepth", metricsService.getQueueDepth("low"));
        
        return ResponseEntity.ok(metrics);
    }
}
