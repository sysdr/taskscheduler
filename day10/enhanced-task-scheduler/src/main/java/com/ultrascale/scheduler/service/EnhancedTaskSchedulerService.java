package com.ultrascale.scheduler.service;

import com.ultrascale.scheduler.model.TaskDefinition;
import com.ultrascale.scheduler.model.TaskResult;
import com.ultrascale.scheduler.model.TaskStatus;
import com.ultrascale.scheduler.model.TaskType;
import com.ultrascale.scheduler.repository.TaskDefinitionRepository;
import com.ultrascale.scheduler.repository.TaskResultRepository;
import com.ultrascale.scheduler.wrapper.CallableTaskWrapper;
import com.ultrascale.scheduler.wrapper.RunnableTaskWrapper;
import com.ultrascale.scheduler.wrapper.TaskWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EnhancedTaskSchedulerService {
    
    @Autowired
    private TaskDefinitionRepository taskDefinitionRepository;
    
    @Autowired
    private TaskResultRepository taskResultRepository;
    
    @Autowired
    private SampleCalculationTask calculationTask;
    
    @Autowired
    private SampleEmailTask emailTask;
    
    public List<TaskDefinition> getAllTasks() {
        return taskDefinitionRepository.findAll();
    }
    
    public Optional<TaskDefinition> getTaskById(Long id) {
        return taskDefinitionRepository.findById(id);
    }
    
    public TaskDefinition createTask(TaskDefinition taskDefinition) {
        return taskDefinitionRepository.save(taskDefinition);
    }
    
    public TaskDefinition updateTask(Long id, TaskDefinition taskDefinition) {
        if (taskDefinitionRepository.existsById(id)) {
            taskDefinition.setId(id);
            return taskDefinitionRepository.save(taskDefinition);
        }
        throw new RuntimeException("Task not found with id: " + id);
    }
    
    public void deleteTask(Long id) {
        taskDefinitionRepository.deleteById(id);
    }
    
    @Async("taskExecutor")
    public CompletableFuture<TaskResult> executeTaskAsync(Long taskId) {
        Optional<TaskDefinition> taskOpt = taskDefinitionRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }
        
        TaskDefinition task = taskOpt.get();
        TaskWrapper wrapper = createTaskWrapper(task);
        TaskResult result = wrapper.execute();
        
        // Save the result
        result = taskResultRepository.save(result);
        
        return CompletableFuture.completedFuture(result);
    }
    
    private TaskWrapper createTaskWrapper(TaskDefinition task) {
        switch (task.getType()) {
            case CALCULATION:
                return new CallableTaskWrapper<>(task, () -> calculationTask.performCalculation());
            case EMAIL:
                return new CallableTaskWrapper<>(task, () -> 
                    emailTask.sendEmail("test@example.com", "Scheduled Task", "This is a scheduled email task"));
            default:
                return new RunnableTaskWrapper(task, () -> {
                    // Default task execution
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
        }
    }
    
    public List<TaskResult> getTaskResults(Long taskId) {
        return taskResultRepository.findByTaskDefinitionId(taskId);
    }
    
    public List<TaskResult> getResultsByStatus(TaskStatus status) {
        return taskResultRepository.findByStatus(status);
    }
    
    public List<TaskDefinition> getActiveTasks() {
        return taskDefinitionRepository.findByActiveTrue();
    }
    
    public List<TaskDefinition> searchTasks(String keyword) {
        return taskDefinitionRepository.searchByKeyword(keyword);
    }
}
