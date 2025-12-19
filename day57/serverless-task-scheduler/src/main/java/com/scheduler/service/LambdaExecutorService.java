package com.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class LambdaExecutorService {
    
    private final LambdaAsyncClient lambdaClient;
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Async
    public CompletableFuture<Void> executeTaskAsync(Task task) {
        log.info("Executing task {} via Lambda function: {}", task.getId(), task.getFunctionName());
        
        task.setStatus(TaskStatus.LAMBDA_INVOKED);
        task.setStartedAt(LocalDateTime.now());
        taskRepository.save(task);
        
        // Store in Redis for quick lookup
        redisTemplate.opsForValue().set(
            "lambda:task:" + task.getId(),
            task,
            Duration.ofMinutes(30)
        );
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("taskId", task.getId());
            payload.put("taskType", task.getType());
            payload.put("data", objectMapper.readValue(task.getPayload(), Object.class));
            
            String payloadJson = objectMapper.writeValueAsString(payload);
            
            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(task.getFunctionName())
                    .invocationType(InvocationType.EVENT) // Async invocation
                    .payload(SdkBytes.fromString(payloadJson, StandardCharsets.UTF_8))
                    .build();
            
            return lambdaClient.invoke(invokeRequest)
                    .thenAccept(response -> {
                        log.info("Lambda invoked successfully: statusCode={}, requestId={}", 
                                response.statusCode(), response.responseMetadata().requestId());
                        
                        task.setLambdaRequestId(response.responseMetadata().requestId());
                        taskRepository.save(task);
                    })
                    .exceptionally(throwable -> {
                        log.error("Lambda invocation failed for task {}: {}", task.getId(), throwable.getMessage());
                        handleLambdaFailure(task, throwable);
                        return null;
                    });
            
        } catch (Exception e) {
            log.error("Error preparing Lambda payload for task {}: {}", task.getId(), e.getMessage());
            handleLambdaFailure(task, e);
            return CompletableFuture.completedFuture(null);
        }
    }
    
    public void handleLambdaCallback(Long taskId, String result, boolean success) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        
        task.setCompletedAt(LocalDateTime.now());
        task.setExecutionTimeMs(
            Duration.between(task.getStartedAt(), task.getCompletedAt()).toMillis()
        );
        
        if (success) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setResult(result);
            
            // Calculate estimated cost (simplified)
            // Lambda pricing: $0.0000166667 per GB-second
            double gbSeconds = (task.getExecutionTimeMs() / 1000.0) * 1.0; // Assume 1GB memory
            task.setEstimatedCost(gbSeconds * 0.0000166667);
            
            log.info("Task {} completed via Lambda in {}ms, cost: ${}", 
                    taskId, task.getExecutionTimeMs(), task.getEstimatedCost());
        } else {
            if (task.getRetryCount() < task.getMaxRetries()) {
                task.setStatus(TaskStatus.RETRYING);
                task.setRetryCount(task.getRetryCount() + 1);
                task.setErrorMessage(result);
                log.warn("Task {} failed, will retry (attempt {}/{})", 
                        taskId, task.getRetryCount(), task.getMaxRetries());
            } else {
                task.setStatus(TaskStatus.FAILED);
                task.setErrorMessage(result);
                log.error("Task {} failed after {} retries", taskId, task.getMaxRetries());
            }
        }
        
        taskRepository.save(task);
        redisTemplate.delete("lambda:task:" + taskId);
    }
    
    private void handleLambdaFailure(Task task, Throwable throwable) {
        task.setErrorMessage(throwable.getMessage());
        
        if (task.getRetryCount() < task.getMaxRetries()) {
            task.setStatus(TaskStatus.RETRYING);
            task.setRetryCount(task.getRetryCount() + 1);
        } else {
            task.setStatus(TaskStatus.FAILED);
        }
        
        taskRepository.save(task);
    }
    
    public void warmLambdaFunctions() {
        log.info("Warming Lambda functions...");
        
        String[] functions = {"image-processor", "report-generator"};
        
        for (String function : functions) {
            try {
                InvokeRequest request = InvokeRequest.builder()
                        .functionName(function)
                        .invocationType(InvocationType.REQUEST_RESPONSE)
                        .payload(SdkBytes.fromString("{\"warm\": true}", StandardCharsets.UTF_8))
                        .build();
                
                lambdaClient.invoke(request).join();
                log.info("Warmed function: {}", function);
            } catch (Exception e) {
                log.warn("Failed to warm function {}: {}", function, e.getMessage());
            }
        }
    }
}
