package com.taskscheduler.consumer.service;

import com.taskscheduler.consumer.config.RabbitConfig;
import com.taskscheduler.consumer.model.TaskExecutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TaskConsumer.class);
    
    @Autowired
    private TaskProcessor taskProcessor;
    
    private int processedCount = 0;
    
    @RabbitListener(queues = RabbitConfig.TASK_EXECUTION_QUEUE)
    public void consumeTaskExecutionRequest(TaskExecutionRequest request) {
        processedCount++;
        logger.info("Received task execution request #{}: {}", processedCount, request);
        
        try {
            taskProcessor.processTask(request);
        } catch (Exception e) {
            logger.error("Error processing task {}: {}", request.getTaskId(), e.getMessage(), e);
            // In a real system, you might want to send to a dead letter queue
        }
    }
    
    public int getProcessedCount() {
        return processedCount;
    }
}
