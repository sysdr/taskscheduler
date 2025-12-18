package com.scheduler.service;

import com.scheduler.model.ScheduledTask;
import com.scheduler.model.ScheduledTask.TaskStatus;
import com.scheduler.repository.ScheduledTaskRepository;
import com.scheduler.repository.TenantRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskSchedulerService {
    
    private final ScheduledTaskRepository taskRepository;
    private final TenantRepository tenantRepository;
    private final ResourceGovernor resourceGovernor;
    private final ExecutorService executorService;
    
    public TaskSchedulerService(
            ScheduledTaskRepository taskRepository,
            TenantRepository tenantRepository,
            ResourceGovernor resourceGovernor) {
        this.taskRepository = taskRepository;
        this.tenantRepository = tenantRepository;
        this.resourceGovernor = resourceGovernor;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }
    
    @Scheduled(fixedRate = 5000) // Poll every 5 seconds
    public void pollAndExecuteTasks() {
        tenantRepository.findAll().stream()
            .filter(tenant -> tenant.getActive())
            .forEach(tenant -> {
                String tenantId = tenant.getTenantId();
                
                List<ScheduledTask> tasksToExecute = taskRepository.findTasksToExecute(
                    tenantId, TaskStatus.PENDING, LocalDateTime.now());
                
                tasksToExecute.forEach(task -> {
                    if (resourceGovernor.canExecuteTask(tenantId)) {
                        executeTask(task);
                    }
                });
            });
    }
    
    @Transactional
    public void executeTask(ScheduledTask task) {
        task.setStatus(TaskStatus.RUNNING);
        task.setLastRunTime(LocalDateTime.now());
        taskRepository.save(task);
        
        resourceGovernor.incrementRunningTasks(task.getTenantId());
        
        executorService.submit(() -> {
            try {
                // Simulate task execution
                Thread.sleep(2000);
                
                task.setStatus(TaskStatus.COMPLETED);
                task.setExecutionCount(task.getExecutionCount() + 1);
                task.setLastResult("Success: Task completed for tenant " + task.getTenantId());
                
                // Calculate next run time (simple: add 1 hour)
                task.setNextRunTime(LocalDateTime.now().plusHours(1));
                
            } catch (Exception e) {
                task.setStatus(TaskStatus.FAILED);
                task.setLastResult("Error: " + e.getMessage());
            } finally {
                taskRepository.save(task);
                resourceGovernor.decrementRunningTasks(task.getTenantId());
            }
        });
    }
}
