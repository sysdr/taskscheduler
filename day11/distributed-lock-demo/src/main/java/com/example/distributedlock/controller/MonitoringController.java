package com.example.distributedlock.controller;

import com.example.distributedlock.model.TaskExecutionLog;
import com.example.distributedlock.model.TaskExecutionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {
    
    @Autowired
    private TaskExecutionLogRepository logRepository;
    
    @GetMapping("/executions/{taskName}")
    public List<TaskExecutionLog> getTaskExecutions(@PathVariable String taskName) {
        return logRepository.findByTaskNameOrderByExecutionTimeDesc(taskName);
    }
    
    @GetMapping("/recent/{taskName}")
    public Map<String, Object> getRecentExecutions(@PathVariable String taskName) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<TaskExecutionLog> recentExecutions = logRepository.findRecentExecutions(taskName, oneMinuteAgo);
        long count = logRepository.countRecentExecutions(taskName, oneMinuteAgo);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskName", taskName);
        result.put("executionsInLastMinute", count);
        result.put("executions", recentExecutions);
        result.put("hasDuplicates", count > 1);
        
        return result;
    }
    
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Check for recent duplicates
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        
        long criticalTaskCount = logRepository.countRecentExecutions("CriticalBusinessTask", oneMinuteAgo);
        long financialTaskCount = logRepository.countRecentExecutions("DailyFinancialCalculation", oneMinuteAgo);
        
        dashboard.put("criticalTaskExecutions", criticalTaskCount);
        dashboard.put("financialTaskExecutions", financialTaskCount);
        dashboard.put("criticalTaskHasDuplicates", criticalTaskCount > 1);
        dashboard.put("financialTaskHasDuplicates", financialTaskCount > 1);
        dashboard.put("timestamp", LocalDateTime.now());
        
        return dashboard;
    }
    
    @GetMapping("/all")
    public List<TaskExecutionLog> getAllExecutions() {
        return logRepository.findAll();
    }
}
