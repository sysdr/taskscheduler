package com.taskscheduler.consumer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;

public class Task {
    private final String id;
    private final String type;
    private final Map<String, Object> payload;
    private final int priority;
    private final LocalDateTime createdAt;
    private final LocalDateTime scheduledAt;
    private final int retryCount;
    private final int maxRetries;

    @JsonCreator
    public Task(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("payload") Map<String, Object> payload,
            @JsonProperty("priority") int priority,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("scheduledAt") LocalDateTime scheduledAt,
            @JsonProperty("retryCount") int retryCount,
            @JsonProperty("maxRetries") int maxRetries) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.priority = priority;
        this.createdAt = createdAt;
        this.scheduledAt = scheduledAt;
        this.retryCount = retryCount;
        this.maxRetries = maxRetries;
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public Map<String, Object> getPayload() { return payload; }
    public int getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public int getRetryCount() { return retryCount; }
    public int getMaxRetries() { return maxRetries; }

    public Task withIncrementedRetry() {
        return new Task(id, type, payload, priority, createdAt, scheduledAt, 
                       retryCount + 1, maxRetries);
    }

    public boolean hasRetriesLeft() {
        return retryCount < maxRetries;
    }
}
