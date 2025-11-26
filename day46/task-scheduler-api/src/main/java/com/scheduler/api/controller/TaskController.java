package com.scheduler.api.controller;

import com.scheduler.api.dto.*;
import com.scheduler.api.model.TaskStatus;
import com.scheduler.api.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", task));
    }
    
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TaskStatus status) {
        return ResponseEntity.ok(taskService.getAllTasks(page, size, sortBy, name, status));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<ExecutionResponse>> executeTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.executeTask(id));
    }
    
    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<TaskResponse>> pauseTask(@PathVariable Long id) {
        TaskResponse task = taskService.pauseTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task paused successfully", task));
    }
    
    @PostMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<TaskResponse>> resumeTask(@PathVariable Long id) {
        TaskResponse task = taskService.resumeTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task resumed successfully", task));
    }
    
    @GetMapping("/{id}/statistics")
    public ResponseEntity<ApiResponse<TaskStatistics>> getTaskStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskStatistics(id)));
    }
}
