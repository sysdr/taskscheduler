package com.taskscheduler.consumer.handler;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;
import com.taskscheduler.consumer.monitoring.ConsumerMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MessageAcknowledgmentHandler {
    private static final Logger log = LoggerFactory.getLogger(MessageAcknowledgmentHandler.class);
    private final ConsumerMetrics metrics;
    private final DeadLetterHandler deadLetterHandler;
    private final RetryHandler retryHandler;

    public MessageAcknowledgmentHandler(ConsumerMetrics metrics, 
                                      DeadLetterHandler deadLetterHandler,
                                      RetryHandler retryHandler) {
        this.metrics = metrics;
        this.deadLetterHandler = deadLetterHandler;
        this.retryHandler = retryHandler;
    }

    public void handleProcessingResult(Task task, ProcessingResult result, 
                                     Acknowledgment acknowledgment,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset) {
        
        switch (result.getStatus()) {
            case SUCCESS:
                handleSuccess(task, result, acknowledgment, topic, partition, offset);
                break;
            case RETRYABLE_FAILURE:
                handleRetryableFailure(task, result, acknowledgment, topic, partition, offset);
                break;
            case PERMANENT_FAILURE:
                handlePermanentFailure(task, result, acknowledgment, topic, partition, offset);
                break;
        }
    }

    private void handleSuccess(Task task, ProcessingResult result, Acknowledgment acknowledgment,
                             String topic, int partition, long offset) {
        log.info("✅ Task {} processed successfully in {}ms", 
                task.getId(), result.getProcessingTimeMs());
        
        // Acknowledge the message - it's been processed successfully
        acknowledgment.acknowledge();
        
        // Update metrics
        metrics.incrementSuccessfulTasks();
        metrics.recordProcessingTime(result.getProcessingTimeMs());
        
        log.debug("Message acknowledged for task {} (topic: {}, partition: {}, offset: {})", 
                 task.getId(), topic, partition, offset);
    }

    private void handleRetryableFailure(Task task, ProcessingResult result, Acknowledgment acknowledgment,
                                      String topic, int partition, long offset) {
        log.warn("⚠️ Task {} failed with retryable error: {}", 
                task.getId(), result.getMessage());
        
        if (task.hasRetriesLeft()) {
            // Send to retry topic with exponential backoff
            Task retryTask = task.withIncrementedRetry();
            retryHandler.scheduleRetry(retryTask, calculateRetryDelay(retryTask.getRetryCount()));
            
            // Acknowledge original message since we've handled it (by scheduling retry)
            acknowledgment.acknowledge();
            
            metrics.incrementRetriedTasks();
            log.info("📋 Task {} scheduled for retry #{} with delay", 
                    task.getId(), retryTask.getRetryCount());
        } else {
            // No more retries - send to dead letter queue
            log.error("💀 Task {} exhausted all retries, sending to dead letter queue", task.getId());
            handlePermanentFailure(task, result, acknowledgment, topic, partition, offset);
        }
    }

    private void handlePermanentFailure(Task task, ProcessingResult result, Acknowledgment acknowledgment,
                                      String topic, int partition, long offset) {
        log.error("💀 Task {} permanently failed: {}", task.getId(), result.getMessage());
        
        // Send to dead letter queue
        deadLetterHandler.sendToDeadLetterQueue(task, result);
        
        // Acknowledge the message - we've handled it by sending to DLQ
        acknowledgment.acknowledge();
        
        // Update metrics
        metrics.incrementFailedTasks();
        
        log.info("Message sent to dead letter queue for task {} (topic: {}, partition: {}, offset: {})", 
                task.getId(), topic, partition, offset);
    }

    private long calculateRetryDelay(int retryCount) {
        // Exponential backoff: 2^retryCount seconds (with jitter)
        long baseDelay = (long) Math.pow(2, retryCount) * 1000; // Convert to milliseconds
        long jitter = (long) (Math.random() * 1000); // Add up to 1 second jitter
        return Math.min(baseDelay + jitter, 60000); // Cap at 1 minute
    }
}
