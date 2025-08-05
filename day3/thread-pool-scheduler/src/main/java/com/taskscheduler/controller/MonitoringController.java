package com.taskscheduler.controller;

import com.taskscheduler.model.TaskExecutionMetrics;
import com.taskscheduler.service.MonitoringService;
import com.taskscheduler.service.ScheduledTasksService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class MonitoringController {
    
    private final MonitoringService monitoringService;
    private final ScheduledTasksService scheduledTasksService;
    
    public MonitoringController(MonitoringService monitoringService, ScheduledTasksService scheduledTasksService) {
        this.monitoringService = monitoringService;
        this.scheduledTasksService = scheduledTasksService;
    }
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("threadPoolStats", monitoringService.getThreadPoolStats());
        model.addAttribute("taskMetrics", scheduledTasksService.getMetrics());
        return "dashboard";
    }
    
    @GetMapping("/api/thread-pool-stats")
    @ResponseBody
    public ResponseEntity<MonitoringService.ThreadPoolStats> getThreadPoolStats() {
        return ResponseEntity.ok(monitoringService.getThreadPoolStats());
    }
    
    @GetMapping("/api/task-metrics")
    @ResponseBody
    public ResponseEntity<Map<String, TaskExecutionMetrics>> getTaskMetrics() {
        return ResponseEntity.ok(scheduledTasksService.getMetrics());
    }
}
