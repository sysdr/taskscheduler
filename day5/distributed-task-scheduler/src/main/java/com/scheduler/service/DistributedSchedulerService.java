package com.scheduler.service;

import com.scheduler.model.TaskDefinition;
import com.scheduler.model.TaskExecution;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskDefinitionRepository;
import com.scheduler.repository.TaskExecutionRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DistributedSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(DistributedSchedulerService.class);
    
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskExecutorService taskExecutorService;
    private final String instanceId;
    
    public DistributedSchedulerService(TaskDefinitionRepository taskDefinitionRepository,
                                     TaskExecutionRepository taskExecutionRepository,
                                     TaskExecutorService taskExecutorService,
                                     @Value("${spring.application.name:unknown}") String appName) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskExecutionRepository = taskExecutionRepository;
        this.taskExecutorService = taskExecutorService;
        this.instanceId = appName + "-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Set instance ID in MDC for logging
        MDC.put("instanceId", instanceId);
        
        logger.info("Distributed Scheduler initialized with instance ID: {}", instanceId);
    }
    
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    @SchedulerLock(name = "taskDiscovery", lockAtMostFor = "29s", lockAtLeastFor = "5s")
    public void discoverAndExecuteTasks() {
        try {
            logger.debug("Starting task discovery cycle...");
            
            List<TaskDefinition> eligibleTasks = taskDefinitionRepository.findEligibleTasks(LocalDateTime.now());
            
            logger.info("Found {} eligible tasks for execution", eligibleTasks.size());
            
            for (TaskDefinition task : eligibleTasks) {
                try {
                    executeTaskIfNotRunning(task);
                } catch (Exception e) {
                    logger.error("Error processing task {}: {}", task.getName(), e.getMessage(), e);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error in task discovery cycle: {}", e.getMessage(), e);
        }
    }
    
    @Transactional
    protected void executeTaskIfNotRunning(TaskDefinition taskDef) {
        // Double-check if task is already running
        long runningCount = taskExecutionRepository.countRunningExecutions();
        
        if (runningCount > 0) {
            logger.debug("Task {} is already running, skipping", taskDef.getName());
            return;
        }
        
        // Create execution record
        TaskExecution execution = new TaskExecution(taskDef.getName(), instanceId);
        taskExecutionRepository.save(execution);
        
        // Update task status
        taskDef.setStatus(TaskStatus.RUNNING);
        taskDef.setLastExecutedAt(LocalDateTime.now());
        taskDefinitionRepository.save(taskDef);
        
        logger.info("Starting execution of task: {} on instance: {}", taskDef.getName(), instanceId);
        
        // Execute task asynchronously
        taskExecutorService.executeTask(taskDef, execution);
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    @Scheduled(fixedDelay = 60000) // Every minute
    public void logSchedulerStats() {
        long activeTasks = taskDefinitionRepository.countActiveTasks();
        long runningTasks = taskDefinitionRepository.countRunningTasks();
        long runningExecutions = taskExecutionRepository.countRunningExecutions();
        
        logger.info("Scheduler Stats - Active Tasks: {}, Running Tasks: {}, Running Executions: {}", 
                   activeTasks, runningTasks, runningExecutions);
    }
}
