package com.scheduler.logging.controller;

import com.scheduler.logging.model.LogEntry;
import com.scheduler.logging.model.LogEntryRepository;
import com.scheduler.logging.model.Task;
import com.scheduler.logging.model.TaskRepository;
import com.scheduler.logging.service.LoggingStatsService;
import com.scheduler.logging.service.TaskExecutorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final TaskRepository taskRepository;
    private final LogEntryRepository logEntryRepository;
    private final TaskExecutorService taskExecutorService;
    private final LoggingStatsService statsService;
    
    public ApiController(TaskRepository taskRepository,
                         LogEntryRepository logEntryRepository,
                         TaskExecutorService taskExecutorService,
                         LoggingStatsService statsService) {
        this.taskRepository = taskRepository;
        this.logEntryRepository = logEntryRepository;
        this.taskExecutorService = taskExecutorService;
        this.statsService = statsService;
    }
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return statsService.getStats();
    }
    
    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return taskRepository.findRecentTasks();
    }
    
    @PostMapping("/tasks")
    public Task createTask(@RequestBody Map<String, String> request) {
        return taskExecutorService.createTask(
            request.get("taskType"),
            request.get("payload")
        );
    }
    
    @GetMapping("/logs")
    public List<LogEntry> getLogs() {
        return logEntryRepository.findRecentLogs();
    }
    
    @GetMapping("/logs/level/{level}")
    public List<LogEntry> getLogsByLevel(@PathVariable String level) {
        return logEntryRepository.findByLevelOrderByTimestampDesc(level.toUpperCase());
    }
}
