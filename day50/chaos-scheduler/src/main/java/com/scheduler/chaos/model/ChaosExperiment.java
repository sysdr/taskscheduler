package com.scheduler.chaos.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChaosExperiment {
    private String id;
    private ChaosType type;
    private ExperimentStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private ChaosConfig config;
    
    public enum ChaosType {
        LEADER_KILL,
        NETWORK_PARTITION,
        LATENCY_INJECTION,
        RESOURCE_EXHAUSTION,
        MESSAGE_QUEUE_SATURATION,
        DATABASE_SLOWDOWN,
        SPLIT_BRAIN,
        CASCADE_FAILURE
    }
    
    public enum ExperimentStatus {
        SCHEDULED,
        RUNNING,
        COMPLETED,
        FAILED,
        STOPPED
    }
}
