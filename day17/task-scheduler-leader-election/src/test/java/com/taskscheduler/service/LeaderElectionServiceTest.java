package com.taskscheduler.service;

import com.taskscheduler.repository.LeaderElectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class LeaderElectionServiceTest {
    
    @Autowired
    private LeaderElectionService leaderElectionService;
    
    @Autowired
    private LeaderElectionRepository repository;
    
    @Test
    public void testInstanceIdGeneration() {
        String instanceId = leaderElectionService.getInstanceId();
        assertNotNull(instanceId);
        assertTrue(instanceId.length() > 10);
    }
    
    @Test
    public void testLeaderElectionInitialization() {
        assertNotNull(leaderElectionService);
        // Initially should not be leader (leadership acquired via scheduled tasks)
        assertFalse(leaderElectionService.isLeader());
    }
}
