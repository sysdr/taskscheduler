package com.scheduler.service;

import com.scheduler.model.Leader;
import com.scheduler.repository.LeaderRepository;
import com.scheduler.repository.NodeHealthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "scheduler.node.id=test-node",
    "scheduler.leader.lease-duration=15000"
})
class LeaderElectionServiceTest {

    @MockBean
    private LeaderRepository leaderRepository;

    @MockBean 
    private NodeHealthRepository nodeHealthRepository;

    @MockBean
    private HealthMonitorService healthMonitorService;

    @Test
    void testLeaderElectionWhenNoLeaderExists() {
        when(leaderRepository.findCurrentLeader()).thenReturn(Optional.empty());
        
        LeaderElectionService service = new LeaderElectionService(
            leaderRepository, nodeHealthRepository, healthMonitorService);
        
        service.attemptLeadershipAcquisition();
        
        verify(leaderRepository, atLeastOnce()).save(any(Leader.class));
    }

    @Test
    void testLeadershipRenewal() {
        Leader existingLeader = new Leader("test-node", LocalDateTime.now().plusSeconds(10));
        when(leaderRepository.findCurrentLeader()).thenReturn(Optional.of(existingLeader));
        
        LeaderElectionService service = new LeaderElectionService(
            leaderRepository, nodeHealthRepository, healthMonitorService);
        
        service.renewLeadership();
        
        verify(leaderRepository).save(existingLeader);
        assertTrue(existingLeader.getLeaseExpiresAt().isAfter(LocalDateTime.now()));
    }
}
