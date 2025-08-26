package com.ultrascale.scheduler.controller;

import com.ultrascale.scheduler.model.TaskDefinition;
import com.ultrascale.scheduler.model.TaskResult;
import com.ultrascale.scheduler.model.TaskStatus;
import com.ultrascale.scheduler.service.EnhancedTaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private EnhancedTaskSchedulerService taskSchedulerService;
    
    @GetMapping
    public ResponseEntity<List<TaskDefinition>> getAllTasks() {
        List<TaskDefinition> tasks = taskSchedulerService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinition> getTaskById(@PathVariable Long id) {
        return taskSchedulerService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<TaskDefinition> createTask(@RequestBody TaskDefinition taskDefinition) {
        TaskDefinition createdTask = taskSchedulerService.createTask(taskDefinition);
        return ResponseEntity.ok(createdTask);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinition> updateTask(@PathVariable Long id, @RequestBody TaskDefinition taskDefinition) {
        try {
            TaskDefinition updatedTask = taskSchedulerService.updateTask(id, taskDefinition);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskSchedulerService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<CompletableFuture<TaskResult>> executeTask(@PathVariable Long id) {
        try {
            CompletableFuture<TaskResult> future = taskSchedulerService.executeTaskAsync(id);
            return ResponseEntity.ok(future);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/results")
    public ResponseEntity<List<TaskResult>> getTaskResults(@PathVariable Long id) {
        List<TaskResult> results = taskSchedulerService.getTaskResults(id);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResult>> getResultsByStatus(@PathVariable TaskStatus status) {
        List<TaskResult> results = taskSchedulerService.getResultsByStatus(status);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<TaskDefinition>> getActiveTasks() {
        List<TaskDefinition> activeTasks = taskSchedulerService.getActiveTasks();
        return ResponseEntity.ok(activeTasks);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TaskDefinition>> searchTasks(@RequestParam String keyword) {
        List<TaskDefinition> tasks = taskSchedulerService.searchTasks(keyword);
        return ResponseEntity.ok(tasks);
    }
}
