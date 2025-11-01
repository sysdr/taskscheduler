package com.taskscheduler.controller;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.service.TaskSchedulerService;
import com.taskscheduler.service.TaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskSchedulerService taskSchedulerService;
    
    @Autowired
    private TaskExecutionService taskExecutionService;
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskSchedulerService.getAllTasks());
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        Task task = taskSchedulerService.getTask(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> createTask(@RequestBody Task task) {
        String taskId = taskSchedulerService.scheduleTask(task);
        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("message", "Task scheduled successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getTaskStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("scheduled", taskSchedulerService.getScheduledTasksCount());
        stats.put("queued", taskSchedulerService.getQueuedTasksCount());
        stats.put("processing", taskSchedulerService.getProcessingTasksCount());
        stats.put("completed", taskSchedulerService.getCompletedTasksCount());
        stats.put("failed", taskSchedulerService.getFailedTasksCount());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/executions")
    public ResponseEntity<List<TaskExecution>> getAllExecutions() {
        return ResponseEntity.ok(taskExecutionService.getAllExecutions());
    }
    
    @GetMapping("/{taskId}/executions")
    public ResponseEntity<List<TaskExecution>> getTaskExecutions(@PathVariable String taskId) {
        return ResponseEntity.ok(taskExecutionService.getExecutionsByTaskId(taskId));
    }
}
