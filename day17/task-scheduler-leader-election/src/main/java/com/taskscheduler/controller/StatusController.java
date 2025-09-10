package com.taskscheduler.controller;

import com.taskscheduler.service.LeaderElectionService;
import com.taskscheduler.service.TaskProcessorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {
    
    private final LeaderElectionService leaderElectionService;
    private final TaskProcessorService taskProcessorService;
    
    public StatusController(LeaderElectionService leaderElectionService, 
                          TaskProcessorService taskProcessorService) {
        this.leaderElectionService = leaderElectionService;
        this.taskProcessorService = taskProcessorService;
    }
    
    @GetMapping
    public Map<String, Object> getStatus() {
        return Map.of(
            "instanceId", leaderElectionService.getInstanceId(),
            "isLeader", leaderElectionService.isLeader(),
            "currentLeader", leaderElectionService.getCurrentLeader(),
            "processedTasks", taskProcessorService.getProcessedTaskCount()
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> getHealth() {
        return Map.of(
            "status", "UP",
            "instance", leaderElectionService.getInstanceId()
        );
    }
}
