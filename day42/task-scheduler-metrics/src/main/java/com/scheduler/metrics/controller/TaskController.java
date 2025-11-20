package com.scheduler.metrics.controller;

import com.scheduler.metrics.model.Task;
import com.scheduler.metrics.model.TaskStatus;
import com.scheduler.metrics.repository.TaskRepository;
import com.scheduler.metrics.service.TaskExecutorService;
import com.scheduler.metrics.service.TaskMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskRepository taskRepository;
    private final TaskExecutorService executorService;
    private final TaskMetricsService metricsService;
    
    public TaskController(TaskRepository taskRepository,
                          TaskExecutorService executorService,
                          TaskMetricsService metricsService) {
        this.taskRepository = taskRepository;
        this.executorService = executorService;
        this.metricsService = metricsService;
    }
    
    @PostMapping
    public ResponseEntity<Task> submitTask(@RequestBody TaskRequest request) {
        Task task = executorService.submitTask(
                request.name(), 
                request.type(), 
                request.priority()
        );
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/retry")
    public ResponseEntity<String> retryTask(@PathVariable Long id) {
        executorService.retryTask(id);
        return ResponseEntity.ok("Task queued for retry");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total", taskRepository.count());
        stats.put("queued", taskRepository.countByStatus(TaskStatus.QUEUED));
        stats.put("executing", taskRepository.countByStatus(TaskStatus.EXECUTING));
        stats.put("completed", taskRepository.countByStatus(TaskStatus.COMPLETED));
        stats.put("failed", taskRepository.countByStatus(TaskStatus.FAILED));
        stats.put("activeGauge", metricsService.getActiveTaskCount());
        stats.put("queuedGauge", metricsService.getQueuedTaskCount());
        stats.put("avgExecutionTime", taskRepository.getAverageExecutionTime());
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/generate/{count}")
    public ResponseEntity<String> generateTasks(@PathVariable int count) {
        String[] types = {"email", "report", "notification", "export"};
        String[] priorities = {"HIGH", "MEDIUM", "LOW"};
        
        for (int i = 0; i < count; i++) {
            String type = types[i % types.length];
            String priority = priorities[i % priorities.length];
            executorService.submitTask(
                    "Task-" + System.currentTimeMillis() + "-" + i,
                    type,
                    priority
            );
        }
        
        return ResponseEntity.ok("Generated " + count + " tasks");
    }
    
    public record TaskRequest(String name, String type, String priority) {}
}
