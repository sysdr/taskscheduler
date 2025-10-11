package com.scheduler.controller;

import com.scheduler.dto.TaskCreateRequest;
import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskCreateRequest request) {
        Task task = new Task(request.getName(), request.getPayload());
        
        if (request.getScheduledAt() != null) {
            task.setScheduledAt(request.getScheduledAt());
        }
        
        if (request.getMaxRetries() != null) {
            task.setMaxRetries(request.getMaxRetries());
        }
        
        Task savedTask = taskService.createTask(task);
        return ResponseEntity.ok(savedTask);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable String id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }
    
    @PostMapping("/demo/create-test-tasks")
    public ResponseEntity<String> createTestTasks() {
        // Create various types of tasks for demonstration
        taskService.createTask(new Task("email-success", "{\"to\":\"user@example.com\",\"subject\":\"Welcome\"}", LocalDateTime.now()));
        taskService.createTask(new Task("email-timeout", "{\"to\":\"user@timeout.com\",\"subject\":\"Will timeout\"}", LocalDateTime.now()));
        taskService.createTask(new Task("data-validation", "{\"invalid\":\"payload\"}", LocalDateTime.now()));
        taskService.createTask(new Task("network-fail", "{\"endpoint\":\"unreachable.service.com\"}", LocalDateTime.now()));
        taskService.createTask(new Task("random-fail", "{\"data\":\"might fail randomly\"}", LocalDateTime.now()));
        taskService.createTask(new Task("process-success", "{\"action\":\"process_order\",\"order_id\":123}", LocalDateTime.now().plusSeconds(5)));
        
        return ResponseEntity.ok("Created 6 demo tasks with various success/failure scenarios");
    }
}
