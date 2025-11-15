package com.taskscheduler.model;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public abstract class BaseEvent {
    private String eventId = UUID.randomUUID().toString();
    private String eventType;
    private String version = "1.0";
    private Instant timestamp = Instant.now();
    private String source;
}
