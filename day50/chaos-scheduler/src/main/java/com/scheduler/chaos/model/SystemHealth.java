package com.scheduler.chaos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemHealth {
    private String leaderNode;
    private int activeNodes;
    private long taskThroughput;
    private double errorRate;
    private long avgLatencyMs;
    private boolean isHealthy;
    private String status;
}
