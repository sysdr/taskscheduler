package com.scheduler.chaos.controller;

import com.scheduler.chaos.model.*;
import com.scheduler.chaos.service.ChaosService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chaos")
@RequiredArgsConstructor
public class ChaosController {
    private final ChaosService chaosService;
    
    @PostMapping("/start")
    public ChaosExperiment startExperiment(@RequestBody ChaosRequest request) {
        ChaosConfig config = new ChaosConfig();
        config.setDurationSeconds(request.getDurationSeconds());
        config.setLatencyMs(request.getLatencyMs());
        config.setFailureRate(request.getFailureRate());
        config.setAutoRecover(true);
        
        return chaosService.startExperiment(request.getType(), config);
    }
    
    @PostMapping("/stop/{id}")
    public void stopExperiment(@PathVariable String id) {
        chaosService.stopExperiment(id);
    }
    
    @GetMapping("/health")
    public SystemHealth getHealth() {
        return chaosService.getSystemHealth();
    }
    
    @GetMapping("/metrics")
    public ChaosMetrics getMetrics() {
        return chaosService.getMetrics();
    }
    
    @GetMapping("/experiments")
    public List<ChaosExperiment> getExperiments() {
        return chaosService.getActiveExperiments();
    }
}

@lombok.Data
class ChaosRequest {
    private ChaosExperiment.ChaosType type;
    private int durationSeconds = 30;
    private int latencyMs = 1000;
    private int failureRate = 10;
}
