package com.taskscheduler.service;

import com.taskscheduler.dto.TaskDefinitionCreateRequest;
import com.taskscheduler.dto.TaskDefinitionResponse;
import com.taskscheduler.dto.TaskDefinitionSearchRequest;
import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.exception.TaskNameAlreadyExistsException;
import com.taskscheduler.exception.TaskNotFoundException;
import com.taskscheduler.repository.TaskDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskDefinitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDefinitionService.class);
    
    private final TaskDefinitionRepository repository;
    
    public TaskDefinitionService(TaskDefinitionRepository repository) {
        this.repository = repository;
    }
    
    public TaskDefinitionResponse createTask(TaskDefinitionCreateRequest request) {
        logger.info("Creating new task: {}", request.name());
        
        // Check if task name already exists
        if (repository.existsByNameIgnoreCaseAndIdNot(request.name(), null)) {
            throw new TaskNameAlreadyExistsException(request.name());
        }
        
        TaskDefinition task = request.toEntity();
        TaskDefinition savedTask = repository.save(task);
        
        logger.info("Task created successfully with id: {}", savedTask.getId());
        return TaskDefinitionResponse.fromEntity(savedTask);
    }
    
    @Transactional(readOnly = true)
    public TaskDefinitionResponse getTaskById(Long id) {
        logger.debug("Retrieving task with id: {}", id);
        
        TaskDefinition task = repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        
        return TaskDefinitionResponse.fromEntity(task);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinitionResponse> getAllTasks() {
        logger.debug("Retrieving all tasks");
        
        List<TaskDefinition> tasks = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return tasks.stream()
            .map(TaskDefinitionResponse::fromEntity)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<TaskDefinitionResponse> searchTasks(TaskDefinitionSearchRequest searchRequest) {
        logger.debug("Searching tasks with criteria: {}", searchRequest);
        
        // Create sort object
        Sort sort = Sort.by(
            searchRequest.sortDir().equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC,
            searchRequest.sortBy()
        );
        
        Pageable pageable = PageRequest.of(searchRequest.page(), searchRequest.size(), sort);
        
        Page<TaskDefinition> tasksPage = repository.findWithFilters(
            searchRequest.status(),
            searchRequest.namePattern(),
            searchRequest.startDate(),
            searchRequest.endDate(),
            pageable
        );
        
        return tasksPage.map(TaskDefinitionResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDefinitionResponse> getTasksByStatus(TaskDefinition.TaskStatus status) {
        logger.debug("Retrieving tasks with status: {}", status);
        
        List<TaskDefinition> tasks = repository.findByStatus(status);
        return tasks.stream()
            .map(TaskDefinitionResponse::fromEntity)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public long getTaskCountByStatus(TaskDefinition.TaskStatus status) {
        return repository.countByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public boolean taskNameExists(String name) {
        return repository.existsByNameIgnoreCaseAndIdNot(name, null);
    }
}
