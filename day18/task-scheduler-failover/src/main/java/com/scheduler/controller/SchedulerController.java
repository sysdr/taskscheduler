package com.scheduler.controller;

import com.scheduler.model.Leader;
import com.scheduler.model.NodeHealth;
import com.scheduler.service.HealthMonitorService;
import com.scheduler.service.LeaderElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduler")
@CrossOrigin(origins = "*")
public class SchedulerController {
    
    private final LeaderElectionService leaderElectionService;
    private final HealthMonitorService healthMonitorService;
    
    public SchedulerController(LeaderElectionService leaderElectionService,
                             HealthMonitorService healthMonitorService) {
        this.leaderElectionService = leaderElectionService;
        this.healthMonitorService = healthMonitorService;
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("nodeId", leaderElectionService.getNodeId());
            status.put("isLeader", leaderElectionService.isLeader());
            
            try {
                Optional<Leader> currentLeader = leaderElectionService.getCurrentLeader();
                if (currentLeader.isPresent()) {
                    status.put("currentLeader", currentLeader.get().getNodeId());
                    status.put("leaderGeneration", currentLeader.get().getGeneration());
                    status.put("leaderExpiry", currentLeader.get().getLeaseExpiresAt());
                } else {
                    status.put("currentLeader", "None");
                    status.put("leaderGeneration", 0);
                    status.put("leaderExpiry", null);
                }
            } catch (Exception e) {
                status.put("currentLeader", "Error");
                status.put("leaderGeneration", 0);
                status.put("leaderExpiry", null);
                status.put("error", e.getMessage());
            }
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }
    
    @GetMapping("/leader")
    public ResponseEntity<Map<String, Object>> getCurrentLeader() {
        try {
            Optional<Leader> leader = leaderElectionService.getCurrentLeader();
            if (leader.isPresent()) {
                Map<String, Object> leaderData = new HashMap<>();
                leaderData.put("id", leader.get().getId());
                leaderData.put("nodeId", leader.get().getNodeId());
                leaderData.put("generation", leader.get().getGeneration());
                leaderData.put("leaseExpiresAt", leader.get().getLeaseExpiresAt());
                leaderData.put("lastHeartbeat", leader.get().getLastHeartbeat());
                return ResponseEntity.ok(leaderData);
            } else {
                Map<String, Object> noLeader = new HashMap<>();
                noLeader.put("message", "No leader found");
                return ResponseEntity.ok(noLeader);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<List<NodeHealth>> getAllNodeHealth() {
        List<NodeHealth> nodeHealths = healthMonitorService.getAllNodeHealth();
        return ResponseEntity.ok(nodeHealths);
    }
    
    @GetMapping("/health/healthy")
    public ResponseEntity<List<NodeHealth>> getHealthyNodes() {
        List<NodeHealth> healthyNodes = healthMonitorService.getHealthyNodes();
        return ResponseEntity.ok(healthyNodes);
    }
    
    @PostMapping("/election/trigger")
    public ResponseEntity<Map<String, String>> triggerElection() {
        leaderElectionService.attemptLeadershipAcquisition();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Election triggered for node: " + leaderElectionService.getNodeId());
        return ResponseEntity.ok(response);
    }
}
