package com.scheduler.controller;

import com.scheduler.metrics.TaskMetrics;
import com.scheduler.model.Task;
import com.scheduler.model.TaskRepository;
import com.scheduler.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final TaskMetrics taskMetrics;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
        Task task = taskService.submitTask(request.name, request.type);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", taskRepository.count());
        stats.put("active", taskMetrics.getActiveTaskCount());
        stats.put("queued", taskMetrics.getQueueDepth());
        stats.put("byStatus", taskRepository.countByStatus());
        stats.put("byType", taskRepository.countByType());
        return ResponseEntity.ok(stats);
    }

    public record TaskRequest(String name, String type) {}
}
