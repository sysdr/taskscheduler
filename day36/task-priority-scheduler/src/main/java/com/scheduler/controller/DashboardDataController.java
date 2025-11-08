package com.scheduler.controller;

import com.scheduler.dto.DashboardMetrics;
import com.scheduler.service.DemoDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardDataController {

    private final DemoDataService demoDataService;

    public DashboardDataController(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @GetMapping("/demo")
    public DashboardMetrics getDemoDashboard() {
        return demoDataService.getDemoMetrics();
    }
}


