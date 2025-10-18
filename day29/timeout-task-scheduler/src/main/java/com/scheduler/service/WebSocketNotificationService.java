package com.scheduler.service;

import com.scheduler.model.TaskExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyTaskSubmitted(TaskExecution execution) {
        sendTaskUpdate(execution, "Task submitted");
    }

    public void notifyTaskStarted(TaskExecution execution) {
        sendTaskUpdate(execution, "Task started");
    }

    public void notifyTaskCompleted(TaskExecution execution) {
        sendTaskUpdate(execution, "Task completed successfully");
    }

    public void notifyTaskTimedOut(TaskExecution execution) {
        sendTaskUpdate(execution, "Task timed out after " + execution.getTimeout().toSeconds() + " seconds");
    }

    public void notifyTaskFailed(TaskExecution execution) {
        sendTaskUpdate(execution, "Task failed: " + execution.getError());
    }

    public void notifyTaskCancelled(TaskExecution execution) {
        sendTaskUpdate(execution, "Task cancelled");
    }

    public void notifyTaskWarning(TaskExecution execution) {
        sendTaskUpdate(execution, "Warning: Task approaching timeout threshold");
    }

    private void sendTaskUpdate(TaskExecution execution, String message) {
        Map<String, Object> update = new HashMap<>();
        update.put("taskId", execution.getTaskId());
        update.put("taskType", execution.getTaskType());
        update.put("status", execution.getStatus().name());
        update.put("message", message);
        update.put("elapsedSeconds", execution.getElapsedTime().toSeconds());
        update.put("timeoutSeconds", execution.getTimeout().toSeconds());
        update.put("timestamp", execution.getSubmittedAt().format(FORMATTER));

        messagingTemplate.convertAndSend("/topic/task-updates", update);
    }
}
