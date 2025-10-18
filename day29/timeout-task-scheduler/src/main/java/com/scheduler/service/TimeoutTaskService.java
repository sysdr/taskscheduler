package com.scheduler.service;

import com.scheduler.model.TaskRequest;
import com.scheduler.model.TaskExecution;
import com.scheduler.model.TaskStatus;
import com.scheduler.monitor.TaskTimeoutMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class TimeoutTaskService {
    
    private final Map<String, TaskExecution> activeTasks = new ConcurrentHashMap<>();
    private final TaskTimeoutMonitor timeoutMonitor;
    private final WebSocketNotificationService notificationService;
    
    @Value("${scheduler.timeout.default-timeout-seconds:30}")
    private int defaultTimeoutSeconds;

    @Autowired
    public TimeoutTaskService(TaskTimeoutMonitor timeoutMonitor, 
                             WebSocketNotificationService notificationService) {
        this.timeoutMonitor = timeoutMonitor;
        this.notificationService = notificationService;
    }

    public String submitTask(TaskRequest request) {
        CompletableFuture<String> future = executeTaskWithTimeout(request);
        TaskExecution execution = new TaskExecution(request, future);
        
        activeTasks.put(request.taskId(), execution);
        timeoutMonitor.registerTask(execution);
        
        notificationService.notifyTaskSubmitted(execution);
        
        // Handle completion asynchronously
        future.whenComplete((result, throwable) -> {
            handleTaskCompletion(request.taskId(), result, throwable);
        });
        
        return request.taskId();
    }

    public CompletableFuture<String> executeTaskWithTimeout(TaskRequest request) {
        CompletableFuture<String> taskFuture = CompletableFuture.supplyAsync(() -> {
            TaskExecution execution = activeTasks.get(request.taskId());
            if (execution != null) {
                execution.setStatus(TaskStatus.RUNNING);
                execution.setStartedAt(LocalDateTime.now());
                notificationService.notifyTaskStarted(execution);
            }
            
            return executeBusinessLogic(request);
        });

        // Create a timeout future
        CompletableFuture<String> timeoutFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(request.timeout().toSeconds() * 1000);
                return "TIMEOUT: Task exceeded " + request.timeout().toSeconds() + " seconds";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "TIMEOUT: Task exceeded " + request.timeout().toSeconds() + " seconds";
            }
        });

        // Return whichever completes first
        return CompletableFuture.anyOf(taskFuture, timeoutFuture)
            .thenApply(result -> {
                if (result instanceof String) {
                    String resultStr = (String) result;
                    if (resultStr.startsWith("TIMEOUT:")) {
                        // Cancel the original task if it's still running
                        taskFuture.cancel(true);
                        return resultStr;
                    }
                    return resultStr;
                }
                return "ERROR: Unexpected result type";
            })
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof TimeoutException) {
                    taskFuture.cancel(true);
                    return "TIMEOUT: Task exceeded " + request.timeout().toSeconds() + " seconds";
                }
                return "ERROR: " + throwable.getMessage();
            });
    }

    private String executeBusinessLogic(TaskRequest request) {
        try {
            switch (request.taskType()) {
                case "FAST_TASK":
                    Thread.sleep(1000); // 1 second
                    return "Fast task completed: " + request.payload();
                    
                case "MEDIUM_TASK":
                    Thread.sleep(5000); // 5 seconds
                    return "Medium task completed: " + request.payload();
                    
                case "SLOW_TASK":
                    Thread.sleep(15000); // 15 seconds
                    return "Slow task completed: " + request.payload();
                    
                case "INFINITE_TASK":
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(100); // Infinite loop that can be interrupted
                    }
                    throw new InterruptedException("Infinite task was interrupted");
                    
                case "RANDOM_DURATION":
                    int duration = (int) (Math.random() * 20000) + 1000; // 1-21 seconds
                    Thread.sleep(duration);
                    return "Random task completed after " + duration + "ms: " + request.payload();
                    
                default:
                    Thread.sleep(3000); // Default 3 seconds
                    return "Default task completed: " + request.payload();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task interrupted", e);
        }
    }

    private void handleTaskCompletion(String taskId, String result, Throwable throwable) {
        TaskExecution execution = activeTasks.get(taskId);
        if (execution == null) return;

        execution.setCompletedAt(LocalDateTime.now());
        
        // Check if this is a timeout result (either from exception or timeout string)
        if (throwable != null && throwable.getCause() instanceof TimeoutException) {
            // For infinite tasks, mark as cancelled; for others, mark as timed out
            if ("INFINITE_TASK".equals(execution.getTaskType())) {
                execution.setStatus(TaskStatus.CANCELLED);
                execution.setError("Task cancelled due to timeout");
                notificationService.notifyTaskCancelled(execution);
            } else {
                execution.setStatus(TaskStatus.TIMED_OUT);
                execution.setError("Task timed out");
                notificationService.notifyTaskTimedOut(execution);
            }
        } else if (result != null && result.startsWith("TIMEOUT:")) {
            // For infinite tasks, mark as cancelled; for others, mark as timed out
            if ("INFINITE_TASK".equals(execution.getTaskType())) {
                execution.setStatus(TaskStatus.CANCELLED);
                execution.setError("Task cancelled due to timeout");
                execution.setResult(result);
                notificationService.notifyTaskCancelled(execution);
            } else {
                execution.setStatus(TaskStatus.TIMED_OUT);
                execution.setError("Task timed out");
                execution.setResult(result);
                notificationService.notifyTaskTimedOut(execution);
            }
        } else if (throwable != null) {
            execution.setStatus(TaskStatus.FAILED);
            execution.setError(throwable.getMessage());
            notificationService.notifyTaskFailed(execution);
        } else {
            execution.setStatus(TaskStatus.COMPLETED);
            execution.setResult(result);
            notificationService.notifyTaskCompleted(execution);
        }
        
        timeoutMonitor.unregisterTask(taskId);
    }

    public List<TaskExecution> getAllTasks() {
        return new ArrayList<>(activeTasks.values());
    }

    public TaskExecution getTask(String taskId) {
        return activeTasks.get(taskId);
    }

    public boolean cancelTask(String taskId) {
        TaskExecution execution = activeTasks.get(taskId);
        if (execution != null && !execution.getFuture().isDone()) {
            boolean cancelled = execution.getFuture().cancel(true);
            if (cancelled) {
                execution.setStatus(TaskStatus.CANCELLED);
                execution.setCompletedAt(LocalDateTime.now());
                timeoutMonitor.unregisterTask(taskId);
                notificationService.notifyTaskCancelled(execution);
            }
            return cancelled;
        }
        return false;
    }

    public void cleanupCompletedTasks() {
        activeTasks.entrySet().removeIf(entry -> {
            TaskExecution execution = entry.getValue();
            return execution.getFuture().isDone() && 
                   execution.getCompletedAt() != null &&
                   execution.getCompletedAt().isBefore(LocalDateTime.now().minusMinutes(5));
        });
    }
}
