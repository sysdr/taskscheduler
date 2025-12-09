package com.scheduler.integration;

import com.scheduler.leader.LeaderElection;
import com.scheduler.util.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Testcontainers
class LeaderElectionIT {
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @Autowired
    private LeaderElection election;

    @Test
    void testSingleLeader() {
        String id = "inst-1";
        assertTrue(election.electLeader(id));
        assertTrue(election.isLeader(id));
        assertEquals(id, election.getCurrentLeader());
    }

    @Test
    void testMultipleInstancesOneLeader() throws InterruptedException {
        int instances = 5;
        ExecutorService executor = Executors.newFixedThreadPool(instances);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(instances);
        Set<String> leaders = new ConcurrentSkipListSet<>();

        for (int i = 0; i < instances; i++) {
            String id = "inst-" + i;
            executor.submit(() -> {
                try {
                    latch.await();
                    if (election.electLeader(id)) leaders.add(id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        latch.countDown();
        done.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(1, leaders.size());
    }

    @Test
    void testLeaderStepDown() {
        String id = "inst-1";
        assertTrue(election.electLeader(id));
        assertTrue(election.stepDown(id));
        assertFalse(election.isLeader(id));
        assertNull(election.getCurrentLeader());
    }
}
