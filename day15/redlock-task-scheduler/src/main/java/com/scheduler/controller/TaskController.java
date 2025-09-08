package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.service.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskSchedulerService taskSchedulerService;

    @Autowired
    public TaskController(TaskSchedulerService taskSchedulerService) {
        this.taskSchedulerService = taskSchedulerService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskSchedulerService.getAllTasks();
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> taskData) {
        String name = (String) taskData.get("name");
        String description = (String) taskData.get("description");
        LocalDateTime scheduledTime = LocalDateTime.parse((String) taskData.get("scheduledTime"));
        
        Task task = taskSchedulerService.addTask(name, description, scheduledTime);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/instance")
    public ResponseEntity<Map<String, String>> getInstanceInfo() {
        return ResponseEntity.ok(Map.of(
            "instanceId", taskSchedulerService.getInstanceId(),
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
