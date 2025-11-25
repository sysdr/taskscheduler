package com.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {
    private String id;
    private Instant timestamp;
    private String level;
    private String service;
    private String instance;
    private String correlationId;
    private String taskId;
    private String userId;
    private String message;
    private String logger;
    private Map<String, Object> metadata;
}
