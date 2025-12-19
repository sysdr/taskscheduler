package com.scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.model.*;
import com.scheduler.service.LambdaExecutorService;
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
    private final LambdaExecutorService lambdaExecutorService;
    private final ObjectMapper objectMapper;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> request) {
        Task task = new Task();
        task.setName((String) request.get("name"));
        task.setType((String) request.get("type"));
        task.setExecutionMode(ExecutionMode.valueOf((String) request.getOrDefault("executionMode", "AUTO")));
        task.setFunctionName((String) request.getOrDefault("functionName", "image-processor"));
        
        try {
            task.setPayload(objectMapper.writeValueAsString(request.get("payload")));
        } catch (Exception e) {
            task.setPayload("{}");
        }
        
        return ResponseEntity.ok(taskRepository.save(task));
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
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = taskRepository.count();
        long completed = taskRepository.findByStatus(TaskStatus.COMPLETED).size();
        long failed = taskRepository.findByStatus(TaskStatus.FAILED).size();
        long running = taskRepository.findByStatus(TaskStatus.RUNNING).size() + 
                       taskRepository.findByStatus(TaskStatus.LAMBDA_INVOKED).size();
        
        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("failed", failed);
        stats.put("running", running);
        stats.put("pending", total - completed - failed - running);
        
        List<Task> lambdaTasks = taskRepository.findByExecutionMode(ExecutionMode.LAMBDA);
        double totalCost = lambdaTasks.stream()
                .filter(t -> t.getEstimatedCost() != null)
                .mapToDouble(Task::getEstimatedCost)
                .sum();
        
        stats.put("lambdaExecutions", lambdaTasks.size());
        stats.put("totalLambdaCost", String.format("$%.6f", totalCost));
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/{id}/lambda-callback")
    public ResponseEntity<Void> handleLambdaCallback(
            @PathVariable Long id,
            @RequestBody Map<String, Object> callback) {
        
        String result = (String) callback.get("result");
        boolean success = (boolean) callback.getOrDefault("success", false);
        
        lambdaExecutorService.handleLambdaCallback(id, result, success);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/warm-pool")
    public ResponseEntity<Void> warmPool() {
        lambdaExecutorService.warmLambdaFunctions();
        return ResponseEntity.ok().build();
    }
}
