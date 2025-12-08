package com.taskscheduler.controller;

import com.taskscheduler.dto.ErrorResponse;
import com.taskscheduler.dto.TaskRequest;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<Task>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks());
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<Void> executeTask(@PathVariable Long id) {
        taskService.executeTask(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/executions")
    public ResponseEntity<List<TaskExecution>> getTaskExecutions(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskExecutions(id));
    }
    
    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleTaskStatus(@PathVariable Long id) {
        try {
            Task task = taskService.toggleTaskStatus(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(e.getMessage()));
        }
    }
}
