package com.taskscheduler.controller;

import com.taskscheduler.model.TaskDefinition;
import com.taskscheduler.service.TaskDefinitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for TaskDefinition management.
 * Provides HTTP endpoints for CRUD operations and task lifecycle management.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskDefinitionController {
    
    private final TaskDefinitionService taskDefinitionService;
    
    @Autowired
    public TaskDefinitionController(TaskDefinitionService taskDefinitionService) {
        this.taskDefinitionService = taskDefinitionService;
    }
    
    /**
     * Create a new task definition
     */
    @PostMapping
    public ResponseEntity<TaskDefinition> createTask(@Valid @RequestBody TaskDefinition taskDefinition) {
        TaskDefinition createdTask = taskDefinitionService.createTask(taskDefinition);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    
    /**
     * Get all task definitions
     */
    @GetMapping
    public ResponseEntity<List<TaskDefinition>> getAllTasks() {
        List<TaskDefinition> tasks = taskDefinitionService.findAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Get a specific task definition by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinition> getTaskById(@PathVariable String id) {
        return taskDefinitionService.findById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update a task definition
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinition> updateTask(@PathVariable String id, 
                                                    @Valid @RequestBody TaskDefinition updates) {
        return taskDefinitionService.updateTask(id, updates)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete a task definition (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        boolean deleted = taskDefinitionService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * Pause a task
     */
    @PostMapping("/{id}/pause")
    public ResponseEntity<TaskDefinition> pauseTask(@PathVariable String id) {
        return taskDefinitionService.pauseTask(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Resume a task
     */
    @PostMapping("/{id}/resume")
    public ResponseEntity<TaskDefinition> resumeTask(@PathVariable String id) {
        return taskDefinitionService.resumeTask(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Mark task as completed
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskDefinition> completeTask(@PathVariable String id) {
        return taskDefinitionService.markTaskCompleted(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Mark task as failed
     */
    @PostMapping("/{id}/fail")
    public ResponseEntity<TaskDefinition> failTask(@PathVariable String id) {
        return taskDefinitionService.markTaskFailed(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get eligible tasks for execution
     */
    @GetMapping("/eligible")
    public ResponseEntity<List<TaskDefinition>> getEligibleTasks() {
        List<TaskDefinition> eligibleTasks = taskDefinitionService.findEligibleTasks();
        return ResponseEntity.ok(eligibleTasks);
    }
    
    /**
     * Get high priority tasks
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<TaskDefinition>> getHighPriorityTasks() {
        List<TaskDefinition> highPriorityTasks = taskDefinitionService.findHighPriorityTasks();
        return ResponseEntity.ok(highPriorityTasks);
    }
    
    /**
     * Get failed tasks
     */
    @GetMapping("/failed")
    public ResponseEntity<List<TaskDefinition>> getFailedTasks() {
        List<TaskDefinition> failedTasks = taskDefinitionService.findFailedTasks();
        return ResponseEntity.ok(failedTasks);
    }
    
    /**
     * Get task statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<TaskDefinitionService.TaskStatistics> getTaskStatistics() {
        TaskDefinitionService.TaskStatistics statistics = taskDefinitionService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }
}
