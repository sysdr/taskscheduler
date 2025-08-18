package com.taskscheduler.service;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.repository.TaskDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskDefinitionService {
    
    private final TaskDefinitionRepository repository;
    
    @Autowired
    public TaskDefinitionService(TaskDefinitionRepository repository) {
        this.repository = repository;
    }
    
    public TaskDefinition createTask(TaskDefinition taskDefinition) {
        if (repository.existsByName(taskDefinition.getName())) {
            throw new IllegalArgumentException("Task with name '" + 
                taskDefinition.getName() + "' already exists");
        }
        taskDefinition.setCreatedBy("system"); // TODO: Get from security context
        return repository.save(taskDefinition);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskDefinition> findById(Long id) {
        return repository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<TaskDefinition> findByName(String name) {
        return repository.findByName(name);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> findAll() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<TaskDefinition> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> findByType(String type) {
        return repository.findByType(type);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> findActiveEnabledTasks() {
        return repository.findByStatusAndEnabledTrue(TaskDefinition.TaskStatus.ACTIVE);
    }
    
    public TaskDefinition updateTask(Long id, TaskDefinition updatedTask) {
        return repository.findById(id)
            .map(existingTask -> {
                existingTask.setType(updatedTask.getType());
                existingTask.setScheduleExpression(updatedTask.getScheduleExpression());
                existingTask.setPayload(updatedTask.getPayload());
                existingTask.setStatus(updatedTask.getStatus());
                existingTask.setDescription(updatedTask.getDescription());
                existingTask.setEnabled(updatedTask.getEnabled());
                existingTask.setMaxRetries(updatedTask.getMaxRetries());
                existingTask.setUpdatedBy("system"); // TODO: Get from security context
                return repository.save(existingTask);
            })
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }
    
    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    public TaskDefinition activateTask(Long id) {
        return repository.findById(id)
            .map(task -> {
                task.activate();
                task.setUpdatedBy("system");
                return repository.save(task);
            })
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }
    
    public TaskDefinition deactivateTask(Long id) {
        return repository.findById(id)
            .map(task -> {
                task.deactivate();
                task.setUpdatedBy("system");
                return repository.save(task);
            })
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public long getTaskCountByStatus(TaskDefinition.TaskStatus status) {
        return repository.countByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinition> findTasksCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findByCreatedAtBetween(start, end);
    }
}
