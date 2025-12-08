package com.taskscheduler.controller;

import com.taskscheduler.dto.MetricsResponse;
import com.taskscheduler.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    
    @Autowired
    private MetricsService metricsService;
    
    @GetMapping
    public ResponseEntity<MetricsResponse> getMetrics() {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        MetricsResponse metrics = isAdmin 
                ? metricsService.getAdminMetrics()
                : metricsService.getUserMetrics();
        
        return ResponseEntity.ok(metrics);
    }
}

