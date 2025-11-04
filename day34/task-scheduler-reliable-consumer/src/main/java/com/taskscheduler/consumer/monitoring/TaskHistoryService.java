package com.taskscheduler.consumer.monitoring;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class TaskHistoryService {
    private static final int MAX_HISTORY_SIZE = 100;
    private final Deque<TaskHistoryEntry> taskHistory = new ConcurrentLinkedDeque<>();

    public void recordTask(Task task, ProcessingResult result) {
        TaskHistoryEntry entry = new TaskHistoryEntry(
            task.getId(),
            task.getType(),
            result.getStatus(),
            result.getMessage(),
            result.getProcessingTimeMs(),
            LocalDateTime.now()
        );

        taskHistory.addFirst(entry);
        
        // Keep only the last MAX_HISTORY_SIZE entries
        while (taskHistory.size() > MAX_HISTORY_SIZE) {
            taskHistory.removeLast();
        }
    }

    public List<TaskHistoryEntry> getRecentTasks(int limit) {
        return taskHistory.stream()
                .limit(limit)
                .toList();
    }

    public List<TaskHistoryEntry> getAllTasks() {
        return new ArrayList<>(taskHistory);
    }

    public static class TaskHistoryEntry {
        private final String taskId;
        private final String taskType;
        private final ProcessingResult.Status status;
        private final String message;
        private final long processingTimeMs;
        private final LocalDateTime timestamp;

        public TaskHistoryEntry(String taskId, String taskType, ProcessingResult.Status status, 
                               String message, long processingTimeMs, LocalDateTime timestamp) {
            this.taskId = taskId;
            this.taskType = taskType;
            this.status = status;
            this.message = message;
            this.processingTimeMs = processingTimeMs;
            this.timestamp = timestamp;
        }

        public String getTaskId() { return taskId; }
        public String getTaskType() { return taskType; }
        public ProcessingResult.Status getStatus() { return status; }
        public String getMessage() { return message; }
        public long getProcessingTimeMs() { return processingTimeMs; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
