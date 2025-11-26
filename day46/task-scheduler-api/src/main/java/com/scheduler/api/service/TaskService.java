package com.scheduler.api.service;

import com.scheduler.api.dto.*;
import com.scheduler.api.exception.ResourceNotFoundException;
import com.scheduler.api.exception.ValidationException;
import com.scheduler.api.model.*;
import com.scheduler.api.repository.TaskExecutionRepository;
import com.scheduler.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskExecutionRepository executionRepository;
    private final TaskExecutionService taskExecutionService;
    
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        log.info("Creating new task: {}", request.getName());
        
        // Validate task name uniqueness
        if (taskRepository.findByName(request.getName()).isPresent()) {
            throw new ValidationException("Task with name '" + request.getName() + "' already exists");
        }
        
        // Validate cron expression
        validateCronExpression(request.getCronExpression());
        
        Task task = Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cronExpression(request.getCronExpression())
                .enabled(request.getEnabled())
                .status(TaskStatus.ACTIVE)
                .maxRetries(request.getMaxRetries())
                .timeoutSeconds(request.getTimeoutSeconds())
                .payload(request.getPayload())
                .build();
        
        task = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", task.getId());
        
        return mapToResponse(task);
    }
    
    public Page<TaskResponse> getAllTasks(int page, int size, String sortBy, String name, TaskStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        
        Page<Task> tasks;
        if (status != null && name != null && !name.isEmpty()) {
            tasks = taskRepository.findByStatusAndNameContainingIgnoreCase(status, name, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status, pageable);
        } else if (name != null && !name.isEmpty()) {
            tasks = taskRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }
        
        return tasks.map(this::mapToResponse);
    }
    
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return mapToResponse(task);
    }
    
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        log.info("Updating task: {}", id);
        
        Task task = findTaskOrThrow(id);
        
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getCronExpression() != null) {
            validateCronExpression(request.getCronExpression());
            task.setCronExpression(request.getCronExpression());
        }
        if (request.getEnabled() != null) {
            task.setEnabled(request.getEnabled());
        }
        if (request.getMaxRetries() != null) {
            task.setMaxRetries(request.getMaxRetries());
        }
        if (request.getTimeoutSeconds() != null) {
            task.setTimeoutSeconds(request.getTimeoutSeconds());
        }
        if (request.getPayload() != null) {
            task.setPayload(request.getPayload());
        }
        
        task = taskRepository.save(task);
        log.info("Task updated successfully: {}", id);
        
        return mapToResponse(task);
    }
    
    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task: {}", id);
        Task task = findTaskOrThrow(id);
        task.setStatus(TaskStatus.DELETED);
        task.setEnabled(false);
        taskRepository.save(task);
        log.info("Task marked as deleted: {}", id);
    }
    
    @Transactional
    public ApiResponse<ExecutionResponse> executeTask(Long id) {
        log.info("Manually executing task: {}", id);
        
        Task task = findTaskOrThrow(id);
        
        if (task.getStatus() != TaskStatus.ACTIVE) {
            throw new ValidationException("Cannot execute task with status: " + task.getStatus());
        }
        
        TaskExecution execution = taskExecutionService.executeTask(task);
        task.setLastExecutedAt(LocalDateTime.now());
        taskRepository.save(task);
        
        return ApiResponse.success("Task executed successfully", mapToExecutionResponse(execution));
    }
    
    @Transactional
    public TaskResponse pauseTask(Long id) {
        log.info("Pausing task: {}", id);
        Task task = findTaskOrThrow(id);
        task.setStatus(TaskStatus.PAUSED);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }
    
    @Transactional
    public TaskResponse resumeTask(Long id) {
        log.info("Resuming task: {}", id);
        Task task = findTaskOrThrow(id);
        if (task.getStatus() == TaskStatus.DELETED) {
            throw new ValidationException("Cannot resume a deleted task");
        }
        task.setStatus(TaskStatus.ACTIVE);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }
    
    public Page<ExecutionResponse> getExecutions(int page, int size, Long taskId, ExecutionStatus status, 
                                                  LocalDateTime startDate, LocalDateTime endDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        
        Page<TaskExecution> executions;
        if (taskId != null) {
            executions = executionRepository.findByTaskId(taskId, pageable);
        } else if (status != null) {
            executions = executionRepository.findByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            executions = executionRepository.findByStartTimeBetween(startDate, endDate, pageable);
        } else {
            executions = executionRepository.findAll(pageable);
        }
        
        return executions.map(this::mapToExecutionResponse);
    }
    
    public ExecutionResponse getExecutionById(Long id) {
        TaskExecution execution = executionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Execution not found with id: " + id));
        return mapToExecutionResponse(execution);
    }
    
    public TaskStatistics getTaskStatistics(Long taskId) {
        Task task = findTaskOrThrow(taskId);
        
        Long total = executionRepository.countByTaskIdAndStatus(taskId, ExecutionStatus.SUCCESS) +
                     executionRepository.countByTaskIdAndStatus(taskId, ExecutionStatus.FAILED);
        Long successful = executionRepository.countByTaskIdAndStatus(taskId, ExecutionStatus.SUCCESS);
        Long failed = executionRepository.countByTaskIdAndStatus(taskId, ExecutionStatus.FAILED);
        Double avgDuration = executionRepository.getAverageDurationByTaskId(taskId);
        
        // Get last 10 executions
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        List<ExecutionResponse> recentExecutions = executionRepository.findByTaskId(taskId, pageable)
                .getContent()
                .stream()
                .map(this::mapToExecutionResponse)
                .collect(Collectors.toList());
        
        double successRate = total > 0 ? (successful * 100.0 / total) : 0.0;
        
        return TaskStatistics.builder()
                .taskId(taskId)
                .taskName(task.getName())
                .totalExecutions(total)
                .successfulExecutions(successful)
                .failedExecutions(failed)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .averageDurationMs(avgDuration != null ? avgDuration.longValue() : 0L)
                .recentExecutions(recentExecutions)
                .build();
    }
    
    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
    
    private void validateCronExpression(String expression) {
        try {
            CronExpression.parse(expression);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid cron expression: " + e.getMessage());
        }
    }
    
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .cronExpression(task.getCronExpression())
                .enabled(task.getEnabled())
                .status(task.getStatus())
                .maxRetries(task.getMaxRetries())
                .timeoutSeconds(task.getTimeoutSeconds())
                .payload(task.getPayload())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .lastExecutedAt(task.getLastExecutedAt())
                .build();
    }
    
    private ExecutionResponse mapToExecutionResponse(TaskExecution execution) {
        return ExecutionResponse.builder()
                .id(execution.getId())
                .taskId(execution.getTaskId())
                .taskName(execution.getTaskName())
                .status(execution.getStatus())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .durationMs(execution.getDurationMs())
                .result(execution.getResult())
                .errorMessage(execution.getErrorMessage())
                .attemptNumber(execution.getAttemptNumber())
                .build();
    }
}
