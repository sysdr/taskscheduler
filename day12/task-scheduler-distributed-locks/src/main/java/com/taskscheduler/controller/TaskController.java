package com.taskscheduler.controller;

import com.taskscheduler.dto.TaskExecutionRequest;
import com.taskscheduler.dto.TaskExecutionResponse;
import com.taskscheduler.service.TaskExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskExecutionService taskExecutionService;
    
    public TaskController(TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
    }
    
    @PostMapping("/execute")
    public ResponseEntity<TaskExecutionResponse> executeTask(@Valid @RequestBody TaskExecutionRequest request) {
        var execution = taskExecutionService.executeTask(request.taskKey(), request.taskType());
        return ResponseEntity.ok(TaskExecutionResponse.from(execution));
    }
    
    @GetMapping("/history/{taskKey}")
    public ResponseEntity<List<TaskExecutionResponse>> getTaskHistory(@PathVariable String taskKey) {
        var executions = taskExecutionService.getExecutionHistory(taskKey);
        var responses = executions.stream()
            .map(TaskExecutionResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/running")
    public ResponseEntity<List<TaskExecutionResponse>> getRunningTasks() {
        var executions = taskExecutionService.getRunningTasks();
        var responses = executions.stream()
            .map(TaskExecutionResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/stats/completed-count")
    public ResponseEntity<Long> getCompletedTaskCount() {
        return ResponseEntity.ok(taskExecutionService.getCompletedTaskCount());
    }
    
    @GetMapping("/stats/average-time/{taskKey}")
    public ResponseEntity<Double> getAverageExecutionTime(@PathVariable String taskKey) {
        Double avgTime = taskExecutionService.getAverageExecutionTime(taskKey);
        return ResponseEntity.ok(avgTime != null ? avgTime : 0.0);
    }
}
