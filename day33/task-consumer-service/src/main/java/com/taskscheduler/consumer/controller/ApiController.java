package com.taskscheduler.consumer.controller;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.repository.TaskRepository;
import com.taskscheduler.consumer.service.TaskConsumer;
import com.taskscheduler.consumer.service.TaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskConsumer taskConsumer;
    
    @Autowired
    private TaskProcessor taskProcessor;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("workerId", taskProcessor.getWorkerId());
        stats.put("processedCount", taskConsumer.getProcessedCount());
        stats.put("totalTasks", taskRepository.count());
        
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("QUEUED", taskRepository.countByStatus(Task.TaskStatus.QUEUED));
        statusCounts.put("PROCESSING", taskRepository.countByStatus(Task.TaskStatus.PROCESSING));
        statusCounts.put("COMPLETED", taskRepository.countByStatus(Task.TaskStatus.COMPLETED));
        statusCounts.put("FAILED", taskRepository.countByStatus(Task.TaskStatus.FAILED));
        statusCounts.put("RETRY", taskRepository.countByStatus(Task.TaskStatus.RETRY));
        
        stats.put("statusCounts", statusCounts);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        return taskRepository.findByTaskId(taskId)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tasks/status/{status}")
    public ResponseEntity<Iterable<Task>> getTasksByStatus(@PathVariable String status) {
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(taskRepository.findByStatus(taskStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
