package com.scheduler.logging.controller;

import com.scheduler.logging.model.LogEntry;
import com.scheduler.logging.model.LogEntryRepository;
import com.scheduler.logging.model.Task;
import com.scheduler.logging.model.TaskRepository;
import com.scheduler.logging.service.LoggingStatsService;
import com.scheduler.logging.service.TaskExecutorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DashboardController {
    
    private final TaskRepository taskRepository;
    private final LogEntryRepository logEntryRepository;
    private final TaskExecutorService taskExecutorService;
    private final LoggingStatsService statsService;
    
    public DashboardController(TaskRepository taskRepository, 
                               LogEntryRepository logEntryRepository,
                               TaskExecutorService taskExecutorService,
                               LoggingStatsService statsService) {
        this.taskRepository = taskRepository;
        this.logEntryRepository = logEntryRepository;
        this.taskExecutorService = taskExecutorService;
        this.statsService = statsService;
    }
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", statsService.getStats());
        model.addAttribute("tasks", taskRepository.findRecentTasks());
        model.addAttribute("logs", logEntryRepository.findRecentLogs());
        return "dashboard";
    }
    
    @PostMapping("/tasks")
    public String createTask(@RequestParam String taskType, @RequestParam String payload) {
        taskExecutorService.createTask(taskType, payload);
        return "redirect:/";
    }
    
    @GetMapping("/logs/{taskId}")
    @ResponseBody
    public List<LogEntry> getTaskLogs(@PathVariable String taskId) {
        return logEntryRepository.findByTaskIdOrderByTimestampDesc(taskId);
    }
}
