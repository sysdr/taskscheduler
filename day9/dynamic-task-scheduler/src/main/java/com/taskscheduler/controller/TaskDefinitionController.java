package com.taskscheduler.controller;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.repository.TaskDefinitionRepository;
import com.taskscheduler.service.DynamicTaskScheduler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskDefinitionController {
    
    @Autowired
    private TaskDefinitionRepository taskRepository;
    
    @Autowired
    private DynamicTaskScheduler dynamicScheduler;
    
    @GetMapping
    public List<TaskDefinition> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinition> getTaskById(@PathVariable Long id) {
        Optional<TaskDefinition> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<TaskDefinition> createTask(@Valid @RequestBody TaskDefinition task) {
        try {
            if (taskRepository.existsByTaskName(task.getTaskName())) {
                return ResponseEntity.badRequest().build();
            }
            
            TaskDefinition savedTask = taskRepository.save(task);
            
            // If task is active, schedule it immediately
            if (task.getStatus() == TaskDefinition.TaskStatus.ACTIVE) {
                dynamicScheduler.scheduleTask(savedTask);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinition> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDefinition taskDetails) {
        Optional<TaskDefinition> optionalTask = taskRepository.findById(id);
        
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TaskDefinition task = optionalTask.get();
        task.setTaskName(taskDetails.getTaskName());
        task.setDescription(taskDetails.getDescription());
        task.setCronExpression(taskDetails.getCronExpression());
        task.setTaskType(taskDetails.getTaskType());
        task.setTaskData(taskDetails.getTaskData());
        task.setStatus(taskDetails.getStatus());
        
        TaskDefinition updatedTask = taskRepository.save(task);
        
        // Reschedule the task with new parameters
        dynamicScheduler.rescheduleTask(updatedTask);
        
        return ResponseEntity.ok(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Cancel the scheduled task before deletion
        dynamicScheduler.cancelTask(id);
        taskRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}
