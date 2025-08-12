package com.ultrascale.scheduler.demo.controller;

import com.ultrascale.scheduler.demo.model.TaskExecutionRecord;
import com.ultrascale.scheduler.demo.service.TaskExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TaskMonitoringController {
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @Value("${app.instance.id:UNKNOWN}")
    private String instanceId;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        List<TaskExecutionRecord> recentExecutions = taskExecutionRepository.findRecentExecutions(since);
        
        // Group executions by task name
        Map<String, List<TaskExecutionRecord>> executionsByTask = recentExecutions.stream()
            .collect(Collectors.groupingBy(TaskExecutionRecord::getTaskName));
        
        // Calculate duplicate counts
        Map<String, Long> duplicateCounts = new HashMap<>();
        executionsByTask.forEach((taskName, executions) -> {
            duplicateCounts.put(taskName, (long) executions.size());
        });
        
        model.addAttribute("instanceId", instanceId);
        model.addAttribute("recentExecutions", recentExecutions);
        model.addAttribute("executionsByTask", executionsByTask);
        model.addAttribute("duplicateCounts", duplicateCounts);
        
        return "dashboard";
    }
    
    @GetMapping("/api/status")
    @ResponseBody
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("instanceId", instanceId);
        status.put("timestamp", LocalDateTime.now());
        
        LocalDateTime since = LocalDateTime.now().minusMinutes(5);
        status.put("dailyReportExecutions", taskExecutionRepository.countExecutionsSince("DAILY_REPORT_GENERATION", since));
        status.put("billingExecutions", taskExecutionRepository.countExecutionsSince("CUSTOMER_BILLING", since));
        status.put("cleanupExecutions", taskExecutionRepository.countExecutionsSince("DATA_CLEANUP", since));
        
        return status;
    }
}
