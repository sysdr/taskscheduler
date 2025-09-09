package com.scheduler.monitoring.controller;

import com.scheduler.monitoring.service.SchedulerMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {
    
    private final SchedulerMetricsService metricsService;
    
    public MonitoringController(SchedulerMetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return metricsService.getSchedulerStatus();
    }
}
