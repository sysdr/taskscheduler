package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.submitTask(task);
        return ResponseEntity.ok(createdTask);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Task>> getTasksByType(@PathVariable String type) {
        List<Task> tasks = taskService.getTasksByType(type);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/sample/{type}")
    public ResponseEntity<Task> createSampleTask(@PathVariable String type) {
        String[] emailTasks = {"Welcome Email", "Newsletter", "Password Reset", "Account Verification"};
        String[] reportTasks = {"Monthly Report", "Sales Analysis", "User Analytics", "Performance Report"};
        String[] dataTasks = {"Data Backup", "CSV Import", "Database Cleanup", "Data Sync"};
        
        String[] tasks = switch (type.toLowerCase()) {
            case "email" -> emailTasks;
            case "report" -> reportTasks;
            case "data" -> dataTasks;
            default -> dataTasks;
        };
        
        String taskName = tasks[ThreadLocalRandom.current().nextInt(tasks.length)];
        String description = String.format("Sample %s task for demonstration", type);
        Integer processingTime = ThreadLocalRandom.current().nextInt(3, 10);
        
        Task task = new Task(taskName, type, description, processingTime);
        Task createdTask = taskService.submitTask(task);
        return ResponseEntity.ok(createdTask);
    }
}
