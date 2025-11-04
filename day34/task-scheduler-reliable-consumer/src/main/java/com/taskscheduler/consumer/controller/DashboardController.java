package com.taskscheduler.consumer.controller;

import com.taskscheduler.consumer.monitoring.ConsumerMetrics;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {
    private final ConsumerMetrics metrics;

    public DashboardController(ConsumerMetrics metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/api/metrics")
    @ResponseBody
    public Map<String, Object> getMetrics() {
        Map<String, Object> metricsData = new HashMap<>();
        metricsData.put("successfulTasks", metrics.getSuccessfulTasksCount());
        metricsData.put("failedTasks", metrics.getFailedTasksCount());
        metricsData.put("retriedTasks", metrics.getRetriedTasksCount());
        metricsData.put("averageProcessingTime", metrics.getAverageProcessingTime());
        metricsData.put("timestamp", System.currentTimeMillis());
        return metricsData;
    }
}
