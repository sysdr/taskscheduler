package com.taskscheduler.alerting.controller;

import com.taskscheduler.alerting.model.Alert;
import com.taskscheduler.alerting.service.AlertEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    
    private final AlertEvaluationService alertService;
    
    @GetMapping
    public List<Alert> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }
    
    @PostMapping("/{alertId}/acknowledge")
    public Map<String, String> acknowledgeAlert(@PathVariable String alertId) {
        alertService.acknowledgeAlert(alertId);
        return Map.of("status", "acknowledged", "alertId", alertId);
    }
    
    @PostMapping("/{alertId}/silence")
    public Map<String, String> silenceAlert(@PathVariable String alertId) {
        alertService.silenceAlert(alertId);
        return Map.of("status", "silenced", "alertId", alertId);
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "healthy",
            "activeAlerts", alertService.getActiveAlerts().size(),
            "timestamp", System.currentTimeMillis()
        );
    }
}
