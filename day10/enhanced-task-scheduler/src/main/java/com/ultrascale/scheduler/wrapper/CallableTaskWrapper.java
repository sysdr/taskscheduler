package com.ultrascale.scheduler.wrapper;

import com.ultrascale.scheduler.model.TaskDefinition;
import com.ultrascale.scheduler.model.TaskResult;
import com.ultrascale.scheduler.model.TaskStatus;


import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class CallableTaskWrapper<T> implements TaskWrapper {
    
    private final TaskDefinition taskDefinition;
    private final Callable<T> task;
    
    public CallableTaskWrapper(TaskDefinition taskDefinition, Callable<T> task) {
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
            T taskResult = task.call();
            long endTime = System.currentTimeMillis();
            
            result.setStatus(TaskStatus.COMPLETED);
            result.setCompletedAt(LocalDateTime.now());
            result.setExecutionTimeMs(endTime - startTime);
            result.setResult(taskResult != null ? taskResult.toString() : "Task completed with null result");
            
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
