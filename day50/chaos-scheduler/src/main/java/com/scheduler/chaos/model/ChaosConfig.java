package com.scheduler.chaos.model;

import lombok.Data;

@Data
public class ChaosConfig {
    private int durationSeconds;
    private int latencyMs;
    private int failureRate; // percentage
    private String targetService;
    private boolean autoRecover;
}
