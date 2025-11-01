package com.taskscheduler.service;

import com.taskscheduler.model.TaskExecution;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class TaskExecutionService {
    
    private final Map<String, TaskExecution> executions = new ConcurrentHashMap<>();
    
    public void addExecution(TaskExecution execution) {
        executions.put(execution.getExecutionId(), execution);
    }
    
    public TaskExecution getExecution(String executionId) {
        return executions.get(executionId);
    }
    
    public List<TaskExecution> getAllExecutions() {
        return new ArrayList<>(executions.values());
    }
    
    public List<TaskExecution> getExecutionsByTaskId(String taskId) {
        return executions.values().stream()
            .filter(exec -> exec.getTaskId().equals(taskId))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
