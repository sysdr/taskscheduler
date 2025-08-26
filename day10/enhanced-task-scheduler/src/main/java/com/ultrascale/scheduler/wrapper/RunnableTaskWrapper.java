package com.ultrascale.scheduler.wrapper;

import com.ultrascale.scheduler.model.TaskDefinition;
import com.ultrascale.scheduler.model.TaskResult;
import com.ultrascale.scheduler.model.TaskStatus;


import java.time.LocalDateTime;

public class RunnableTaskWrapper implements TaskWrapper {
    
    private final TaskDefinition taskDefinition;
    private final Runnable task;
    
    public RunnableTaskWrapper(TaskDefinition taskDefinition, Runnable task) {
        this.taskDefinition = taskDefinition;
        this.task = task;
    }
    
    @Override
    public TaskResult execute() {
        TaskResult result = new TaskResult();
        result.setTaskDefinition(taskDefinition);
        result.setStatus(TaskStatus.RUNNING);
        result.setStartedAt(LocalDateTime.now());
        
        try {
            long startTime = System.currentTimeMillis();
            task.run();
            long endTime = System.currentTimeMillis();
            
            result.setStatus(TaskStatus.COMPLETED);
            result.setCompletedAt(LocalDateTime.now());
            result.setExecutionTimeMs(endTime - startTime);
            result.setResult("Task executed successfully");
            
        } catch (Exception e) {
            result.setStatus(TaskStatus.FAILED);
            result.setCompletedAt(LocalDateTime.now());
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public String getTaskName() {
        return taskDefinition.getName();
    }
    
    @Override
    public Long getTaskId() {
        return taskDefinition.getId();
    }
}
