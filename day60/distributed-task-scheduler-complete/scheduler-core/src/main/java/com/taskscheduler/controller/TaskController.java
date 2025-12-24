package com.taskscheduler.controller;

import com.taskscheduler.service.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskSchedulerService taskSchedulerService;
    
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Map<String, String>> completeTask(
            @PathVariable Long taskId,
            @RequestParam Long executionId,
            @RequestParam boolean success,
            @RequestParam String message) {
        
        try {
            taskSchedulerService.completeTask(taskId, executionId, success, message);
            return ResponseEntity.ok(Map.of("status", "completed"));
        } catch (Exception e) {
            log.error("Error completing task", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(taskSchedulerService.getAllTasks());
    }
    
    @GetMapping("/{taskId}/executions")
    public ResponseEntity<?> getTaskExecutions(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskSchedulerService.getTaskExecutions(taskId));
    }
}
