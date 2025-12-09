package com.scheduler.leader;

import com.scheduler.lock.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisLeaderElection implements LeaderElection {
    private static final Logger log = LoggerFactory.getLogger(RedisLeaderElection.class);
    private static final String LEADER_KEY = "scheduler:leader";
    private final DistributedLock lock;

    public RedisLeaderElection(DistributedLock lock) {
        this.lock = lock;
    }

    @Override
    public boolean electLeader(String instanceId) {
        boolean elected = lock.tryLock(LEADER_KEY, instanceId, Duration.ofSeconds(30));
        if (elected) log.info("Instance {} elected as leader", instanceId);
        return elected;
    }

    @Override
    public boolean isLeader(String instanceId) {
        return instanceId.equals(lock.getLockOwner(LEADER_KEY));
    }

    @Override
    public String getCurrentLeader() {
        return lock.getLockOwner(LEADER_KEY);
    }

    @Override
    public boolean stepDown(String instanceId) {
        return lock.unlock(LEADER_KEY, instanceId);
    }
}
