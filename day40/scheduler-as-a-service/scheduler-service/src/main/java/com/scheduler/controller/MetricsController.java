package com.scheduler.controller;

import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
@CrossOrigin(origins = "*")
public class MetricsController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @GetMapping
    public Map<String, Object> getMetrics(Authentication authentication) {
        String tenantId = authentication.getName();
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("pending", taskRepository.countByTenantIdAndStatus(tenantId, TaskStatus.PENDING));
        metrics.put("scheduled", taskRepository.countByTenantIdAndStatus(tenantId, TaskStatus.SCHEDULED));
        metrics.put("running", taskRepository.countByTenantIdAndStatus(tenantId, TaskStatus.RUNNING));
        metrics.put("completed", taskRepository.countByTenantIdAndStatus(tenantId, TaskStatus.COMPLETED));
        metrics.put("failed", taskRepository.countByTenantIdAndStatus(tenantId, TaskStatus.FAILED));
        metrics.put("total", taskRepository.findByTenantId(tenantId).size());
        
        return metrics;
    }
}
