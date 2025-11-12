package com.scheduler.backpressure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.backpressure.metrics.MetricsService;
import com.scheduler.backpressure.model.Task;
import com.scheduler.backpressure.service.RateLimiterService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TaskConsumer {
    private final RateLimiterService rateLimiterService;
    private final MetricsService metricsService;
    private final ObjectMapper objectMapper;
    private final AtomicLong processedCount = new AtomicLong(0);
    private Instant lastRateCalculation = Instant.now();
    
    public TaskConsumer(RateLimiterService rateLimiterService, MetricsService metricsService) {
        this.rateLimiterService = rateLimiterService;
        this.metricsService = metricsService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }
    
    @KafkaListener(topics = "task-queue", groupId = "task-consumer-group")
    public void consume(String message) {
        Instant startTime = Instant.now();
        
        try {
            // Apply rate limiting with backpressure
            if (!rateLimiterService.tryAcquire()) {
                metricsService.recordTaskThrottled();
                rateLimiterService.acquire(); // Block until permit available
            }
            
            // Parse and process task
            Task task = objectMapper.readValue(message, Task.class);
            processTask(task);
            
            // Update metrics
            Duration processingTime = Duration.between(startTime, Instant.now());
            metricsService.recordProcessingTime(processingTime);
            metricsService.recordTaskProcessed();
            
            // Calculate current rate
            long count = processedCount.incrementAndGet();
            Instant now = Instant.now();
            long secondsElapsed = Duration.between(lastRateCalculation, now).getSeconds();
            
            if (secondsElapsed >= 1) {
                long rate = count / Math.max(1, secondsElapsed);
                metricsService.updateCurrentRate(rate);
                processedCount.set(0);
                lastRateCalculation = now;
            }
            
        } catch (Exception e) {
            System.err.println("Error processing task: " + e.getMessage());
        }
    }
    
    private void processTask(Task task) {
        // Simulate task processing (database write, API call, etc.)
        try {
            Thread.sleep(50); // Simulate 50ms processing time
            System.out.println("Processed task: " + task.getId() + " - " + task.getType());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
