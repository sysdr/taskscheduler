package com.taskscheduler.service;

import com.taskscheduler.model.ExecutionStatus;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.repository.TaskExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ExecutionTracker {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTracker.class);
    
    @Autowired
    private TaskExecutionRepository repository;

    /**
     * Generates a deterministic execution ID based on task name and parameters
     */
    public String generateExecutionId(String taskName, Object... parameters) {
        try {
            StringBuilder input = new StringBuilder(taskName);
            for (Object param : parameters) {
                if (param != null) {
                    input.append("|").append(param.toString());
                }
            }
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.toString().getBytes());
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().substring(0, 32); // First 32 characters
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available", e);
            return String.valueOf(Math.abs((taskName + String.join("", 
                java.util.Arrays.stream(parameters).map(String::valueOf).toArray(String[]::new))
                ).hashCode()));
        }
    }

    /**
     * Checks if a task execution already exists and returns the result if completed
     */
    @Transactional(readOnly = true)
    public Optional<TaskExecution> checkExistingExecution(String executionId) {
        return repository.findByExecutionId(executionId);
    }

    /**
     * Records the start of a task execution
     */
    @Transactional
    public TaskExecution startExecution(String executionId, String taskName, String parameters) {
        Optional<TaskExecution> existing = repository.findByExecutionId(executionId);
        
        if (existing.isPresent()) {
            TaskExecution execution = existing.get();
            execution.setRetryCount(execution.getRetryCount() + 1);
            execution.setStatus(ExecutionStatus.RUNNING);
            execution.setStartTime(LocalDateTime.now());
            execution.setUpdatedAt(LocalDateTime.now());
            logger.info("Retrying execution: {} (attempt #{})", executionId, execution.getRetryCount() + 1);
            return repository.save(execution);
        } else {
            TaskExecution execution = new TaskExecution(executionId, taskName, parameters);
            execution.setStatus(ExecutionStatus.RUNNING);
            execution.setStartTime(LocalDateTime.now());
            logger.info("Starting new execution: {}", executionId);
            return repository.save(execution);
        }
    }

    /**
     * Records successful completion of a task execution
     */
    @Transactional
    public void recordSuccess(String executionId, String result) {
        repository.findByExecutionId(executionId).ifPresent(execution -> {
            execution.setStatus(ExecutionStatus.COMPLETED);
            execution.setResult(result);
            execution.setEndTime(LocalDateTime.now());
            execution.setUpdatedAt(LocalDateTime.now());
            repository.save(execution);
            logger.info("Execution completed successfully: {}", executionId);
        });
    }

    /**
     * Records failure of a task execution
     */
    @Transactional
    public void recordFailure(String executionId, String errorMessage) {
        repository.findByExecutionId(executionId).ifPresent(execution -> {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(errorMessage);
            execution.setEndTime(LocalDateTime.now());
            execution.setUpdatedAt(LocalDateTime.now());
            repository.save(execution);
            logger.error("Execution failed: {} - {}", executionId, errorMessage);
        });
    }

    /**
     * Marks an execution as skipped (when idempotency check prevents re-execution)
     */
    @Transactional
    public void recordSkipped(String executionId, String reason) {
        repository.findByExecutionId(executionId).ifPresent(execution -> {
            execution.setStatus(ExecutionStatus.SKIPPED);
            execution.setResult(reason);
            execution.setUpdatedAt(LocalDateTime.now());
            repository.save(execution);
            logger.info("Execution skipped: {} - {}", executionId, reason);
        });
    }
}
