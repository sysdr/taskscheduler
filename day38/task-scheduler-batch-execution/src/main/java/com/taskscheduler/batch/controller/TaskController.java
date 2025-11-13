package com.taskscheduler.batch.controller;

import com.taskscheduler.batch.model.Task;
import com.taskscheduler.batch.service.MetricsService;
import com.taskscheduler.batch.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    private final MetricsService metricsService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request.taskType, request.payload);
        return ResponseEntity.ok(task);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<BatchCreateResponse> createBatch(@RequestBody BatchCreateRequest request) {
        List<Task> tasks = new ArrayList<>();
        
        for (int i = 0; i < request.count; i++) {
            String taskType = request.taskTypes.get(i % request.taskTypes.size());
            Task task = taskService.createTask(taskType, "Batch task " + i);
            tasks.add(task);
        }
        
        return ResponseEntity.ok(new BatchCreateResponse(tasks.size(), "Tasks created successfully"));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        TaskService.TaskStats stats = taskService.getStats();
        MetricsService.AggregateMetrics metrics = metricsService.getAggregateMetrics();
        
        return ResponseEntity.ok(Map.of(
                "pending", stats.pending(),
                "processing", stats.processing(),
                "completed", stats.completed(),
                "failed", stats.failed(),
                "retry", stats.retry(),
                "queueSize", stats.queueSize(),
                "avgProcessingTimeMs", metrics.avgProcessingTimeMs(),
                "totalTasksLastHour", metrics.totalTasksLastHour()
        ));
    }
    
    @GetMapping("/batches")
    public ResponseEntity<?> getRecentBatches() {
        return ResponseEntity.ok(metricsService.getRecentBatches());
    }
    
    record CreateTaskRequest(String taskType, String payload) {}
    record BatchCreateRequest(int count, List<String> taskTypes) {}
    record BatchCreateResponse(int count, String message) {}
}
