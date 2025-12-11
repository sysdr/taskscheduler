package com.scheduler.controller;
import com.scheduler.model.*;
import com.scheduler.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api") @CrossOrigin(origins="*")
public class APIController {
    private final TaskQueueService queue;
    private final AutoScalingService scaler;
    
    public APIController(TaskQueueService queue, AutoScalingService scaler) {
        this.queue = queue;
        this.scaler = scaler;
    }
    
    @GetMapping("/metrics")
    public MetricsSnapshot metrics() {
        return MetricsSnapshot.builder()
            .queueDepth(queue.getQueueDepth())
            .activeInstances(scaler.getCurrentInstances())
            .tasksProcessing(queue.getProcessingCount())
            .build();
    }
    
    @GetMapping("/scaling/events")
    public List<ScalingEvent> events() {
        return scaler.getEvents();
    }
}
