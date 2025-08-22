package com.taskscheduler.controller;

import com.taskscheduler.dto.TaskDefinitionCreateRequest;
import com.taskscheduler.dto.TaskDefinitionResponse;
import com.taskscheduler.dto.TaskDefinitionSearchRequest;
import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.service.TaskDefinitionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskDefinitionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDefinitionController.class);
    
    private final TaskDefinitionService taskService;
    
    public TaskDefinitionController(TaskDefinitionService taskService) {
        this.taskService = taskService;
    }
    
    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> createTask(
            @Valid @RequestBody TaskDefinitionCreateRequest request) {
        
        logger.info("POST /tasks - Creating task: {}", request.name());
        
        TaskDefinitionResponse response = taskService.createTask(request);
        
        URI location = URI.create("/api/tasks/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinitionResponse> getTask(@PathVariable Long id) {
        logger.info("GET /tasks/{} - Retrieving task", id);
        
        TaskDefinitionResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TaskDefinitionResponse>> getAllTasks() {
        logger.info("GET /tasks - Retrieving all tasks");
        
        List<TaskDefinitionResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<TaskDefinitionResponse>> searchTasks(
            @RequestParam(required = false) TaskDefinition.TaskStatus status,
            @RequestParam(required = false) String namePattern,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("GET /tasks/search - Searching tasks with filters");
        
        // Parse dates manually for simplicity (in production, use proper date handling)
        var searchRequest = new TaskDefinitionSearchRequest(
            status, namePattern, null, null, page, size, sortBy, sortDir
        );
        
        Page<TaskDefinitionResponse> tasks = taskService.searchTasks(searchRequest);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDefinitionResponse>> getTasksByStatus(
            @PathVariable TaskDefinition.TaskStatus status) {
        
        logger.info("GET /tasks/status/{} - Retrieving tasks by status", status);
        
        List<TaskDefinitionResponse> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getTaskCounts() {
        logger.info("GET /tasks/count - Retrieving task counts");
        
        Map<String, Object> counts = Map.of(
            "active", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.ACTIVE),
            "inactive", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.INACTIVE),
            "paused", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.PAUSED),
            "deleted", taskService.getTaskCountByStatus(TaskDefinition.TaskStatus.DELETED)
        );
        
        return ResponseEntity.ok(counts);
    }
    
    @GetMapping("/exists/{name}")
    public ResponseEntity<Map<String, Boolean>> checkTaskNameExists(@PathVariable String name) {
        logger.info("GET /tasks/exists/{} - Checking if task name exists", name);
        
        boolean exists = taskService.taskNameExists(name);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Task Definition API",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
