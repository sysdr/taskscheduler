package com.scheduler.logging.service;

import com.scheduler.logging.model.LogEntryRepository;
import com.scheduler.logging.model.TaskRepository;
import com.scheduler.logging.model.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoggingStatsService {
    
    private final TaskRepository taskRepository;
    private final LogEntryRepository logEntryRepository;
    
    public LoggingStatsService(TaskRepository taskRepository, LogEntryRepository logEntryRepository) {
        this.taskRepository = taskRepository;
        this.logEntryRepository = logEntryRepository;
    }
    
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Task stats
        stats.put("totalTasks", taskRepository.count());
        stats.put("pendingTasks", taskRepository.countByStatus(Task.TaskStatus.PENDING));
        stats.put("completedTasks", taskRepository.countByStatus(Task.TaskStatus.COMPLETED));
        stats.put("failedTasks", taskRepository.countByStatus(Task.TaskStatus.FAILED));
        stats.put("runningTasks", taskRepository.countByStatus(Task.TaskStatus.RUNNING));
        
        Double avgTime = taskRepository.getAverageExecutionTime();
        stats.put("avgExecutionTimeMs", avgTime != null ? Math.round(avgTime) : 0);
        
        // Log stats
        stats.put("totalLogs", logEntryRepository.count());
        stats.put("errorLogs", logEntryRepository.countByLevel("ERROR"));
        stats.put("warnLogs", logEntryRepository.countByLevel("WARN"));
        stats.put("infoLogs", logEntryRepository.countByLevel("INFO"));
        
        return stats;
    }
}
