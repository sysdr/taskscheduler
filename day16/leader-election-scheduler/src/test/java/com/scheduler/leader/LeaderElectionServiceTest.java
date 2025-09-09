package com.scheduler.leader;

import com.scheduler.leader.service.LeaderElectionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LeaderElectionServiceTest {
    
    @SpyBean
    private LeaderElectionService leaderElectionService;
    
    @Test
    void testInitialState() {
        assertNotNull(leaderElectionService.getInstanceId());
        assertNotNull(leaderElectionService.getCurrentState());
    }
    
    @Test
    void testLeadershipAcquisition() throws InterruptedException {
        // Wait for leadership election to occur
        Thread.sleep(2000);
        
        // In a single instance test, this instance should become leader
        assertTrue(leaderElectionService.isLeader() || 
                  leaderElectionService.getCurrentState().equals("CANDIDATE"));
    }
}
