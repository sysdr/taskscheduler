package com.scheduler.leader.controller;

import com.scheduler.leader.model.Leadership;
import com.scheduler.leader.repository.LeadershipRepository;
import com.scheduler.leader.service.LeaderElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leadership")
public class LeadershipController {
    
    private final LeaderElectionService leaderElectionService;
    private final LeadershipRepository leadershipRepository;
    
    public LeadershipController(LeaderElectionService leaderElectionService,
                              LeadershipRepository leadershipRepository) {
        this.leaderElectionService = leaderElectionService;
        this.leadershipRepository = leadershipRepository;
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLeadershipStatus() {
        Leadership leadership = leadershipRepository.findCurrentLeadership().orElse(null);
        
        return ResponseEntity.ok(Map.of(
            "instanceId", leaderElectionService.getInstanceId(),
            "isLeader", leaderElectionService.isLeader(),
            "state", leaderElectionService.getCurrentState(),
            "currentLeadership", leadership != null ? Map.of(
                "leaderId", leadership.getLeaderId(),
                "leaseStart", leadership.getLeaseStart(),
                "leaseEnd", leadership.getLeaseEnd(),
                "expired", leadership.isExpired()
            ) : null
        ));
    }
    
    @PostMapping("/release")
    public ResponseEntity<Map<String, String>> releaseLeadership() {
        leaderElectionService.releaseLeadership();
        return ResponseEntity.ok(Map.of("message", "Leadership released"));
    }
}
