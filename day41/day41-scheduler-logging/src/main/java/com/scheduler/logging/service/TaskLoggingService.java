package com.scheduler.logging.service;

import com.scheduler.logging.model.LogEntry;
import com.scheduler.logging.model.LogEntryRepository;
import com.scheduler.logging.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskLoggingService {
    private static final Logger log = LoggerFactory.getLogger(TaskLoggingService.class);
    
    private final LogEntryRepository logEntryRepository;
    
    public TaskLoggingService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }
    
    public void setupTaskContext(Task task) {
        MDC.put("taskId", String.valueOf(task.getId()));
        MDC.put("taskType", task.getTaskType());
        MDC.put("correlationId", task.getCorrelationId() != null ? 
            task.getCorrelationId() : UUID.randomUUID().toString());
        MDC.put("executionId", UUID.randomUUID().toString());
    }
    
    public void clearTaskContext() {
        MDC.clear();
    }
    
    public void logTaskCreated(Task task) {
        setupTaskContext(task);
        log.info("Task created: type={}, payload={}", task.getTaskType(), task.getPayload());
        persistLogEntry("INFO", "Task created", null);
    }
    
    public void logTaskStarted(Task task) {
        setupTaskContext(task);
        log.info("Task execution started: attempt={}", task.getRetryCount() + 1);
        persistLogEntry("INFO", "Task execution started (attempt " + (task.getRetryCount() + 1) + ")", null);
    }
    
    public void logTaskCompleted(Task task, long durationMs) {
        setupTaskContext(task);
        log.info("Task completed successfully: duration={}ms", durationMs);
        persistLogEntry("INFO", "Task completed successfully", durationMs);
        clearTaskContext();
    }
    
    public void logTaskFailed(Task task, Throwable error, long durationMs) {
        setupTaskContext(task);
        log.error("Task failed: error={}, duration={}ms", error.getMessage(), durationMs, error);
        persistLogEntry("ERROR", "Task failed: " + error.getMessage(), durationMs);
        clearTaskContext();
    }
    
    public void logTaskRetrying(Task task, int attempt, String reason) {
        setupTaskContext(task);
        log.warn("Task retrying: attempt={}, reason={}", attempt, reason);
        persistLogEntry("WARN", "Task retrying: " + reason, null);
    }
    
    public void logTaskProgress(Task task, String message, Object... args) {
        setupTaskContext(task);
        log.debug(message, args);
        persistLogEntry("DEBUG", String.format(message.replace("{}", "%s"), args), null);
    }
    
    private void persistLogEntry(String level, String message, Long durationMs) {
        LogEntry entry = new LogEntry();
        entry.setLevel(level);
        entry.setTaskId(MDC.get("taskId"));
        entry.setTaskType(MDC.get("taskType"));
        entry.setCorrelationId(MDC.get("correlationId"));
        entry.setMessage(message);
        entry.setDurationMs(durationMs);
        logEntryRepository.save(entry);
    }
}
