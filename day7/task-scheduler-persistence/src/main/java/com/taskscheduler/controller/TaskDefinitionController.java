package com.taskscheduler.controller;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.service.TaskDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskDefinitionController {
    
    private final TaskDefinitionService taskService;
    
    @Autowired
    public TaskDefinitionController(TaskDefinitionService taskService) {
        this.taskService = taskService;
    }
    
    @GetMapping
    public ResponseEntity<Page<TaskDefinition>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskDefinition> tasks = taskService.findAll(pageable);
        
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinition> getTaskById(@PathVariable Long id) {
        return taskService.findById(id)
            .map(task -> ResponseEntity.ok(task))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<TaskDefinition> getTaskByName(@PathVariable String name) {
        return taskService.findByName(name)
            .map(task -> ResponseEntity.ok(task))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TaskDefinition>> getTasksByType(@PathVariable String type) {
        List<TaskDefinition> tasks = taskService.findByType(type);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<TaskDefinition>> getActiveTasks() {
        List<TaskDefinition> tasks = taskService.findActiveEnabledTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping
    public ResponseEntity<TaskDefinition> createTask(@Valid @RequestBody TaskDefinition task) {
        try {
            TaskDefinition createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinition> updateTask(
            @PathVariable Long id, 
            @Valid @RequestBody TaskDefinition task) {
        try {
            TaskDefinition updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<TaskDefinition> activateTask(@PathVariable Long id) {
        try {
            TaskDefinition task = taskService.activateTask(id);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<TaskDefinition> deactivateTask(@PathVariable Long id) {
        try {
            TaskDefinition task = taskService.deactivateTask(id);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getTaskStats() {
        Map<String, Long> stats = Map.of(
            "active", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.ACTIVE),
            "inactive", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.INACTIVE),
            "paused", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.PAUSED),
            "error", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.ERROR)
        );
        return ResponseEntity.ok(stats);
    }
}
