package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.model.User;
import com.taskscheduler.repository.TaskExecutionRepository;
import com.taskscheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DemoDataService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @Transactional
    public void createDemoTasksForUser(User owner) {
        // Check if user already has tasks
        if (!taskRepository.findByOwner(owner).isEmpty()) {
            return; // User already has tasks, skip
        }
        
        createDemoTasks(owner);
    }
    
    private void createDemoTasks(User owner) {
        // Demo Task 1: Daily Backup Task
        Task task1 = new Task();
        task1.setName("Daily Backup");
        task1.setDescription("Automated daily backup of critical system data");
        task1.setCronExpression("0 0 2 * * ?"); // Daily at 2 AM
        task1.setOwner(owner);
        task1.setStatus(Task.TaskStatus.ACTIVE);
        task1.setExecutionCount(5);
        task1.setLastExecuted(LocalDateTime.now().minusHours(12));
        task1.setCreatedAt(LocalDateTime.now().minusDays(7));
        task1 = taskRepository.save(task1);
        
        // Demo executions for task1
        createDemoExecution(task1, TaskExecution.ExecutionStatus.SUCCESS, 
                "Backup completed successfully. Files: 1234, Size: 2.5GB", 
                LocalDateTime.now().minusHours(12), 45000L);
        createDemoExecution(task1, TaskExecution.ExecutionStatus.SUCCESS, 
                "Backup completed successfully. Files: 1230, Size: 2.4GB", 
                LocalDateTime.now().minusDays(1).minusHours(10), 43000L);
        
        // Demo Task 2: Weekly Report
        Task task2 = new Task();
        task2.setName("Weekly Analytics Report");
        task2.setDescription("Generate and email weekly analytics report to stakeholders");
        task2.setCronExpression("0 0 9 ? * MON"); // Every Monday at 9 AM
        task2.setOwner(owner);
        task2.setStatus(Task.TaskStatus.ACTIVE);
        task2.setExecutionCount(3);
        task2.setLastExecuted(LocalDateTime.now().minusDays(3));
        task2.setCreatedAt(LocalDateTime.now().minusDays(14));
        task2 = taskRepository.save(task2);
        
        // Demo executions for task2
        createDemoExecution(task2, TaskExecution.ExecutionStatus.SUCCESS, 
                "Report generated and sent to 5 recipients", 
                LocalDateTime.now().minusDays(3), 12000L);
        
        // Demo Task 3: System Health Check
        Task task3 = new Task();
        task3.setName("System Health Check");
        task3.setDescription("Monitor system health metrics and send alerts if needed");
        task3.setCronExpression("0 */30 * * * ?"); // Every 30 minutes
        task3.setOwner(owner);
        task3.setStatus(Task.TaskStatus.INACTIVE);
        task3.setExecutionCount(0);
        task3.setCreatedAt(LocalDateTime.now().minusDays(2));
        task3 = taskRepository.save(task3);
        
        // Demo Task 4: Database Cleanup
        Task task4 = new Task();
        task4.setName("Database Cleanup");
        task4.setDescription("Remove old log entries and temporary data older than 30 days");
        task4.setCronExpression("0 0 3 * * ?"); // Daily at 3 AM
        task4.setOwner(owner);
        task4.setStatus(Task.TaskStatus.ACTIVE);
        task4.setExecutionCount(10);
        task4.setLastExecuted(LocalDateTime.now().minusDays(1).minusHours(9));
        task4.setCreatedAt(LocalDateTime.now().minusDays(30));
        task4 = taskRepository.save(task4);
        
        // Demo executions for task4
        createDemoExecution(task4, TaskExecution.ExecutionStatus.SUCCESS, 
                "Cleaned up 543 old records. Freed 45MB of space", 
                LocalDateTime.now().minusDays(1).minusHours(9), 8500L);
        createDemoExecution(task4, TaskExecution.ExecutionStatus.SUCCESS, 
                "Cleaned up 521 old records. Freed 43MB of space", 
                LocalDateTime.now().minusDays(2).minusHours(9), 8200L);
        
        // Demo Task 5: Failed Task (for demonstration)
        Task task5 = new Task();
        task5.setName("External API Sync");
        task5.setDescription("Sync data with external API service");
        task5.setCronExpression("0 0 */6 * * ?"); // Every 6 hours
        task5.setOwner(owner);
        task5.setStatus(Task.TaskStatus.FAILED);
        task5.setExecutionCount(8);
        task5.setLastExecuted(LocalDateTime.now().minusHours(4));
        task5.setCreatedAt(LocalDateTime.now().minusDays(5));
        task5 = taskRepository.save(task5);
        
        // Demo executions for task5 - mix of success and failure
        createDemoExecution(task5, TaskExecution.ExecutionStatus.FAILED, 
                LocalDateTime.now().minusHours(4), 30000L,
                null, "Connection timeout: API server not responding");
        createDemoExecution(task5, TaskExecution.ExecutionStatus.SUCCESS, 
                LocalDateTime.now().minusHours(10), 15000L,
                "Successfully synced 234 records", null);
    }
    
    private void createDemoExecution(Task task, TaskExecution.ExecutionStatus status, 
                                     LocalDateTime startTime, Long durationMs,
                                     String result, String errorMessage) {
        TaskExecution execution = new TaskExecution();
        execution.setTask(task);
        execution.setStartTime(startTime);
        execution.setEndTime(startTime.plusSeconds(durationMs / 1000));
        execution.setStatus(status);
        execution.setResult(result);
        execution.setErrorMessage(errorMessage);
        execution.setDurationMs(durationMs);
        taskExecutionRepository.save(execution);
    }
    
    private void createDemoExecution(Task task, TaskExecution.ExecutionStatus status, 
                                     String result, LocalDateTime startTime, Long durationMs) {
        createDemoExecution(task, status, startTime, durationMs, result, null);
    }
}



