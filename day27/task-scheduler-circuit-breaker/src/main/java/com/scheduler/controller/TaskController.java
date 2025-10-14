package com.scheduler.controller;

import com.scheduler.dto.TaskRequest;
import com.scheduler.model.Task;
import com.scheduler.service.TaskService;
import com.scheduler.service.ExternalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ExternalServiceImpl externalService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
        Task task = taskService.createTask(request.getName(), request.getType());
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Task>> getRecentTasks() {
        return ResponseEntity.ok(taskService.getRecentTasks());
    }
    
    @PostMapping("/simulate-failure/{service}")
    public ResponseEntity<String> simulateFailure(@PathVariable String service, @RequestParam boolean enable) {
        switch (service.toLowerCase()) {
            case "payment":
                externalService.setPaymentServiceDown(enable);
                return ResponseEntity.ok("Payment service " + (enable ? "DOWN" : "UP"));
            case "notification":
                externalService.setNotificationServiceFlakey(enable);
                return ResponseEntity.ok("Notification service " + (enable ? "FLAKEY" : "STABLE"));
            default:
                return ResponseEntity.badRequest().body("Unknown service: " + service);
        }
    }
}
