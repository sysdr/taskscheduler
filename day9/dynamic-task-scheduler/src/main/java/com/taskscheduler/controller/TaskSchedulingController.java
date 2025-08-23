package com.taskscheduler.controller;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.repository.TaskDefinitionRepository;
import com.taskscheduler.service.DynamicTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduler")
@CrossOrigin(origins = "*")
public class TaskSchedulingController {
    
    @Autowired
    private DynamicTaskScheduler dynamicScheduler;
    
    @Autowired
    private TaskDefinitionRepository taskRepository;
    
    @PostMapping("/start/{taskId}")
    public ResponseEntity<Map<String, String>> startTask(@PathVariable Long taskId) {
        Optional<TaskDefinition> optionalTask = taskRepository.findById(taskId);
        
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TaskDefinition task = optionalTask.get();
        task.setStatus(TaskDefinition.TaskStatus.ACTIVE);
        taskRepository.save(task);
        
        try {
            dynamicScheduler.scheduleTask(task);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task started successfully");
            response.put("taskName", task.getTaskName());
            response.put("status", "ACTIVE");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to start task: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/stop/{taskId}")
    public ResponseEntity<Map<String, String>> stopTask(@PathVariable Long taskId) {
        Optional<TaskDefinition> optionalTask = taskRepository.findById(taskId);
        
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TaskDefinition task = optionalTask.get();
        task.setStatus(TaskDefinition.TaskStatus.INACTIVE);
        taskRepository.save(task);
        
        dynamicScheduler.cancelTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task stopped successfully");
        response.put("taskName", task.getTaskName());
        response.put("status", "INACTIVE");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/pause/{taskId}")
    public ResponseEntity<Map<String, String>> pauseTask(@PathVariable Long taskId) {
        Optional<TaskDefinition> optionalTask = taskRepository.findById(taskId);
        
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TaskDefinition task = optionalTask.get();
        task.setStatus(TaskDefinition.TaskStatus.PAUSED);
        taskRepository.save(task);
        
        dynamicScheduler.cancelTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task paused successfully");
        response.put("taskName", task.getTaskName());
        response.put("status", "PAUSED");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("activeScheduledTasks", dynamicScheduler.getScheduledTasks().size());
        status.put("totalTasksInDB", taskRepository.count());
        status.put("activeTasks", taskRepository.findActiveTasks().size());
        
        Map<String, Integer> tasksByStatus = new HashMap<>();
        for (TaskDefinition.TaskStatus statusEnum : TaskDefinition.TaskStatus.values()) {
            tasksByStatus.put(statusEnum.name(), taskRepository.findByStatus(statusEnum).size());
        }
        status.put("tasksByStatus", tasksByStatus);
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/reload")
    public ResponseEntity<Map<String, String>> reloadTasks() {
        try {
            // Cancel all current tasks
            dynamicScheduler.getScheduledTasks().forEach((id, future) -> future.cancel(true));
            dynamicScheduler.getScheduledTasks().clear();
            
            // Reload from database
            dynamicScheduler.loadAndScheduleActiveTasks();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Tasks reloaded successfully");
            response.put("activeTasksLoaded", String.valueOf(dynamicScheduler.getScheduledTasks().size()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to reload tasks: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
