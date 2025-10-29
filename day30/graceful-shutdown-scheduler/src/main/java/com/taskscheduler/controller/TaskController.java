package com.taskscheduler.controller;

import com.taskscheduler.component.TaskExecutorService;
import com.taskscheduler.component.TaskSchedulerLifecycleManager;
import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskExecutorService taskExecutorService;
    
    @Autowired
    private TaskSchedulerLifecycleManager lifecycleManager;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        if (!taskExecutorService.isAcceptingNewTasks()) {
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
        
        Task task = taskService.createTask(
            request.getName(),
            request.getDescription(),
            request.getDurationSeconds()
        );
        
        // Execute the task asynchronously
        taskExecutorService.executeTask(task.getId());
        
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/demo-scenario")
    public ResponseEntity<Map<String, Object>> createDemoScenario() {
        if (!taskExecutorService.isAcceptingNewTasks()) {
            return ResponseEntity.status(503).build();
        }
        
        // Create tasks with varying durations
        Task[] tasks = {
            taskService.createTask("Quick Email Send", "Send welcome email", 5),
            taskService.createTask("Data Processing", "Process user data", 15),
            taskService.createTask("Report Generation", "Generate monthly report", 25),
            taskService.createTask("Database Backup", "Backup user database", 45),
            taskService.createTask("Image Processing", "Process uploaded images", 60)
        };
        
        // Start all tasks
        for (Task task : tasks) {
            taskExecutorService.executeTask(task.getId());
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Demo scenario created with 5 tasks of varying durations",
            "tasks", tasks,
            "instruction", "Wait 10 seconds then call POST /api/shutdown to test graceful shutdown"
        ));
    }
    
    @PostMapping("/shutdown")
    public ResponseEntity<Map<String, String>> initiateShutdown() {
        lifecycleManager.initiateGracefulShutdown();
        return ResponseEntity.ok(Map.of(
            "message", "Graceful shutdown initiated",
            "status", "Tasks will complete or be suspended within 30 seconds"
        ));
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        List<Task> allTasks = taskService.getAllTasks();
        
        long pendingCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.PENDING).count();
        long runningCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.RUNNING).count();
        long completedCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count();
        long suspendedCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.SUSPENDED).count();
        long failedCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.FAILED).count();
        
        return ResponseEntity.ok(Map.of(
            "acceptingNewTasks", taskExecutorService.isAcceptingNewTasks(),
            "shutdownInitiated", lifecycleManager.isShutdownInitiated(),
            "totalTasks", allTasks.size(),
            "taskCounts", Map.of(
                "pending", pendingCount,
                "running", runningCount,
                "completed", completedCount,
                "suspended", suspendedCount,
                "failed", failedCount
            )
        ));
    }
    
    public static class CreateTaskRequest {
        private String name;
        private String description;
        private int durationSeconds;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    }
}
