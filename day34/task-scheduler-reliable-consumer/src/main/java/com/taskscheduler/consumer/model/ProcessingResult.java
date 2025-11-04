package com.taskscheduler.consumer.model;

import java.time.LocalDateTime;
import java.util.Optional;

public class ProcessingResult {
    public enum Status {
        SUCCESS, RETRYABLE_FAILURE, PERMANENT_FAILURE
    }

    private final Status status;
    private final String message;
    private final Optional<Throwable> error;
    private final LocalDateTime processedAt;
    private final long processingTimeMs;

    public ProcessingResult(Status status, String message, Throwable error, 
                          LocalDateTime processedAt, long processingTimeMs) {
        this.status = status;
        this.message = message;
        this.error = Optional.ofNullable(error);
        this.processedAt = processedAt;
        this.processingTimeMs = processingTimeMs;
    }

    public static ProcessingResult success(String message, long processingTimeMs) {
        return new ProcessingResult(Status.SUCCESS, message, null, 
                                  LocalDateTime.now(), processingTimeMs);
    }

    public static ProcessingResult retryableFailure(String message, Throwable error, long processingTimeMs) {
        return new ProcessingResult(Status.RETRYABLE_FAILURE, message, error, 
                                  LocalDateTime.now(), processingTimeMs);
    }

    public static ProcessingResult permanentFailure(String message, Throwable error, long processingTimeMs) {
        return new ProcessingResult(Status.PERMANENT_FAILURE, message, error, 
                                  LocalDateTime.now(), processingTimeMs);
    }

    // Getters
    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public Optional<Throwable> getError() { return error; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public long getProcessingTimeMs() { return processingTimeMs; }
}
