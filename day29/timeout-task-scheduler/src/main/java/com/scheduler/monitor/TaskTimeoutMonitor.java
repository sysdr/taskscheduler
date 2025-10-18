package com.scheduler.monitor;

import com.scheduler.model.TaskExecution;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class TaskTimeoutMonitor {
    
    private final Map<String, TaskExecution> monitoredTasks = new ConcurrentHashMap<>();
    private final WebSocketNotificationService notificationService;
    
    @Value("${scheduler.timeout.warning-threshold-percent:80}")
    private double warningThresholdPercent;

    @Autowired
    public TaskTimeoutMonitor(WebSocketNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void registerTask(TaskExecution execution) {
        monitoredTasks.put(execution.getTaskId(), execution);
    }

    public void unregisterTask(String taskId) {
        monitoredTasks.remove(taskId);
    }

    @Scheduled(fixedRate = 1000) // Check every second
    public void checkTimeouts() {
        double warningThreshold = warningThresholdPercent / 100.0;
        
        monitoredTasks.values().forEach(execution -> {
            if (execution.getStatus() == TaskStatus.RUNNING) {
                if (!execution.isWarningIssued() && 
                    execution.isTimeoutApproaching(warningThreshold)) {
                    
                    execution.setWarningIssued(true);
                    execution.setStatus(TaskStatus.WARNING);
                    notificationService.notifyTaskWarning(execution);
                }
            }
        });
    }

    public int getMonitoredTaskCount() {
        return monitoredTasks.size();
    }
}
