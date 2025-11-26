package com.scheduler.api.service;

import com.scheduler.api.model.ExecutionStatus;
import com.scheduler.api.model.Task;
import com.scheduler.api.model.TaskExecution;
import com.scheduler.api.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component("taskExecutionService")
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionService {
    
    private final TaskExecutionRepository executionRepository;
    
    public TaskExecution executeTask(Task task) {
        TaskExecution execution = TaskExecution.builder()
                .taskId(task.getId())
                .taskName(task.getName())
                .status(ExecutionStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .build();
        
        execution = executionRepository.save(execution);
        
        try {
            log.info("Executing task: {} (ID: {})", task.getName(), task.getId());
            
            // Simulate task execution
            String result = performTaskExecution(task);
            
            execution.setStatus(ExecutionStatus.SUCCESS);
            execution.setResult(result);
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(calculateDuration(execution));
            
            log.info("Task executed successfully: {}", task.getName());
            
        } catch (Exception e) {
            log.error("Task execution failed: {}", task.getName(), e);
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(calculateDuration(execution));
        }
        
        return executionRepository.save(execution);
    }
    
    private String performTaskExecution(Task task) throws InterruptedException {
        // Simulate actual work
        int processingTime = (int) (Math.random() * 2000) + 500; // 500-2500ms
        TimeUnit.MILLISECONDS.sleep(processingTime);
        
        // Simulate occasional failures
        if (Math.random() < 0.1) { // 10% failure rate
            throw new RuntimeException("Simulated task execution failure");
        }
        
        return String.format("Task '%s' completed successfully. Processed payload: %s", 
                           task.getName(), task.getPayload());
    }
    
    private Long calculateDuration(TaskExecution execution) {
        if (execution.getStartTime() != null && execution.getEndTime() != null) {
            return java.time.Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis();
        }
        return null;
    }
}
