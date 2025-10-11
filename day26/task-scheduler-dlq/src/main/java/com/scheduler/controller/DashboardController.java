package com.scheduler.controller;

import com.scheduler.dto.DashboardStats;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.DeadLetterService;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private DeadLetterService deadLetterService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        DashboardStats stats = buildDashboardStats();
        model.addAttribute("stats", stats);
        return "dashboard";
    }
    
    @GetMapping("/dlq")
    public String deadLetterQueue(Model model) {
        long unprocessedCount = deadLetterService.getUnprocessedTaskCount();
        Map<String, Long> failureStats = deadLetterService.getFailureReasonStats();
        
        model.addAttribute("unprocessedCount", unprocessedCount);
        model.addAttribute("failureStats", failureStats);
        return "dlq-dashboard";
    }
    
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(buildDashboardStats());
    }
    
    private DashboardStats buildDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalTasks(taskService.getTaskCount());
        stats.setCreatedTasks(taskService.getTaskCountByStatus(TaskStatus.CREATED));
        stats.setProcessingTasks(taskService.getTaskCountByStatus(TaskStatus.PROCESSING));
        stats.setCompletedTasks(taskService.getTaskCountByStatus(TaskStatus.COMPLETED));
        stats.setRetryingTasks(taskService.getTaskCountByStatus(TaskStatus.RETRYING));
        stats.setDeadLetterTasks(taskService.getTaskCountByStatus(TaskStatus.DEAD_LETTER));
        stats.setFailureReasonStats(deadLetterService.getFailureReasonStats());
        
        return stats;
    }
}
