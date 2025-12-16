package com.scheduler.timezone.controller;

import com.scheduler.timezone.dto.ExecutionHistoryResponse;
import com.scheduler.timezone.dto.TaskRequest;
import com.scheduler.timezone.dto.TaskResponse;
import com.scheduler.timezone.service.TaskService;
import com.scheduler.timezone.service.TimeZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    private final TimeZoneService timeZoneService;
    private final com.scheduler.timezone.service.TaskSchedulerService taskSchedulerService;
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/executions")
    public ResponseEntity<List<ExecutionHistoryResponse>> getExecutionHistory(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getExecutionHistory(id));
    }
    
    @GetMapping("/executions/recent")
    public ResponseEntity<List<ExecutionHistoryResponse>> getRecentExecutions() {
        return ResponseEntity.ok(taskService.getRecentExecutions());
    }
    
    @GetMapping("/executions/count")
    public ResponseEntity<Map<String, Long>> getTotalExecutionCount() {
        return ResponseEntity.ok(Map.of("total", taskService.getTotalExecutionCount()));
    }
    
    @GetMapping("/timezones")
    public ResponseEntity<List<String>> getAllTimeZones() {
        return ResponseEntity.ok(timeZoneService.getAllTimeZones());
    }
    
    @GetMapping("/timezones/{zoneId}/info")
    public ResponseEntity<Map<String, String>> getTimeZoneInfo(@PathVariable String zoneId) {
        // Replace only the first underscore with slash to handle timezone IDs like "America_New_York" -> "America/New_York"
        String timeZoneId = zoneId.replaceFirst("_", "/");
        return ResponseEntity.ok(timeZoneService.getTimeZoneInfo(timeZoneId));
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<Map<String, String>> executeTaskManually(@PathVariable String id) {
        try {
            taskSchedulerService.executeTaskManually(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Task executed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
