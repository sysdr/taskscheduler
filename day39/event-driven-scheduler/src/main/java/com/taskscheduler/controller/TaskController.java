package com.taskscheduler.controller;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.repository.TaskRepository;
import com.taskscheduler.service.DemoDataService;
import com.taskscheduler.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskRepository taskRepository;
    private final MetricsService metricsService;
    private final DemoDataService demoDataService;

    @GetMapping("/recent")
    public ResponseEntity<List<Task>> getRecentTasks() {
        return ResponseEntity.ok(taskRepository.findTop20ByOrderByCreatedAtDesc());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskRepository.findByStatus(status));
    }

    @GetMapping("/event-type/{eventType}")
    public ResponseEntity<List<Task>> getTasksByEventType(@PathVariable String eventType) {
        return ResponseEntity.ok(taskRepository.findByEventType(eventType));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> getMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("fileEventsReceived", 
                metricsService.getEventMetrics().getFileEventsReceived().get());
        metrics.put("userEventsReceived", 
                metricsService.getEventMetrics().getUserEventsReceived().get());
        metrics.put("systemEventsReceived", 
                metricsService.getEventMetrics().getSystemEventsReceived().get());
        metrics.put("tasksTriggered", 
                metricsService.getEventMetrics().getTasksTriggered().get());
        metrics.put("tasksCompleted", 
                metricsService.getEventMetrics().getTasksCompleted().get());
        metrics.put("tasksFailed", 
                metricsService.getEventMetrics().getTasksFailed().get());
        metrics.put("deadLetterEvents", 
                metricsService.getEventMetrics().getDeadLetterEvents().get());
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Task> allTasks = taskRepository.findAll();
        stats.put("totalTasks", allTasks.size());
        stats.put("pendingTasks", taskRepository.findByStatus(TaskStatus.PENDING).size());
        stats.put("runningTasks", taskRepository.findByStatus(TaskStatus.RUNNING).size());
        stats.put("completedTasks", taskRepository.findByStatus(TaskStatus.COMPLETED).size());
        stats.put("failedTasks", taskRepository.findByStatus(TaskStatus.FAILED).size());
        stats.put("deadLetterTasks", taskRepository.findByStatus(TaskStatus.DEAD_LETTER).size());
        
        // Calculate average execution time
        double avgExecTime = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .mapToInt(Task::getExecutionTimeMs)
                .average()
                .orElse(0.0);
        stats.put("avgExecutionTimeMs", avgExecTime);
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/demo/generate")
    public ResponseEntity<Map<String, Object>> generateDemoData(
            @RequestParam(defaultValue = "50") int count) {
        List<Task> tasks = demoDataService.generateDemoData(count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Generated " + tasks.size() + " demo tasks");
        response.put("tasksGenerated", tasks.size());
        
        return ResponseEntity.ok(response);
    }
}
