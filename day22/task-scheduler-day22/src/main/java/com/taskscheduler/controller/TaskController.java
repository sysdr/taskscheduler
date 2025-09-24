package com.taskscheduler.controller;

import com.taskscheduler.dto.CreateTaskRequest;
import com.taskscheduler.dto.TaskStatusResponse;
import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.service.StateTransitionService;
import com.taskscheduler.repository.TaskExecutionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for task management operations
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private StateTransitionService stateTransitionService;
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @PostMapping
    public ResponseEntity<TaskStatusResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskExecution task = stateTransitionService.createTask(request.getTaskName(), request.getExecutionDetails());
        return ResponseEntity.ok(TaskStatusResponse.from(task));
    }
    
    @PostMapping("/{taskId}/start")
    public ResponseEntity<TaskStatusResponse> startTask(@PathVariable Long taskId) {
        try {
            TaskExecution task = stateTransitionService.startTask(taskId);
            return ResponseEntity.ok(TaskStatusResponse.from(task));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<TaskStatusResponse> completeTask(@PathVariable Long taskId) {
        try {
            TaskExecution task = stateTransitionService.completeTask(taskId);
            return ResponseEntity.ok(TaskStatusResponse.from(task));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{taskId}/fail")
    public ResponseEntity<TaskStatusResponse> failTask(@PathVariable Long taskId, 
                                                      @RequestParam String errorMessage) {
        try {
            TaskExecution task = stateTransitionService.failTask(taskId, errorMessage);
            return ResponseEntity.ok(TaskStatusResponse.from(task));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskStatusResponse> getTask(@PathVariable Long taskId) {
        try {
            TaskExecution task = stateTransitionService.getTaskExecution(taskId);
            return ResponseEntity.ok(TaskStatusResponse.from(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<TaskStatusResponse>> getAllTasks() {
        List<TaskExecution> tasks = taskExecutionRepository.findAll();
        List<TaskStatusResponse> responses = tasks.stream()
            .map(TaskStatusResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskStatusResponse>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<TaskExecution> tasks = taskExecutionRepository.findByStatus(status);
        List<TaskStatusResponse> responses = tasks.stream()
            .map(TaskStatusResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<Object[]> statusCounts = taskExecutionRepository.getStatusCounts();
        Map<String, Long> statusMap = statusCounts.stream()
            .collect(Collectors.toMap(
                arr -> ((TaskStatus) arr[0]).name(),
                arr -> (Long) arr[1]
            ));
        
        return ResponseEntity.ok(Map.of(
            "statusCounts", statusMap,
            "totalTasks", taskExecutionRepository.count(),
            "averageExecutionTime", taskExecutionRepository.getAverageExecutionTime().orElse(0.0)
        ));
    }
    
    @PostMapping("/cleanup-stale")
    public ResponseEntity<Map<String, Object>> cleanupStaleTasks(@RequestParam(defaultValue = "30") int timeoutMinutes) {
        int cleanedCount = stateTransitionService.cleanupStaleRunningTasks(timeoutMinutes);
        return ResponseEntity.ok(Map.of("cleanedTasksCount", cleanedCount));
    }
}
