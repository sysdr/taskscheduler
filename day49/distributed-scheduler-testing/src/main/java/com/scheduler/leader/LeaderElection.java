package com.scheduler.leader;

public interface LeaderElection {
    boolean electLeader(String instanceId);
    boolean isLeader(String instanceId);
    String getCurrentLeader();
    boolean stepDown(String instanceId);
}
