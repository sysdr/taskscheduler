package com.taskscheduler.service;

import com.taskscheduler.exception.EmailServiceException;
import com.taskscheduler.exception.PermanentTaskException;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final Random random = new Random();
    
    @Retryable(
        value = {EmailServiceException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000)
    )
    public TaskResult sendEmail(Task task) {
        logger.info("Attempting to send email for task: {}", task.getId());
        
        // Simulate various failure scenarios
        int scenario = random.nextInt(10);
        
        if (scenario < 3) {
            // 30% chance of transient failure
            logger.warn("Transient failure occurred for task: {}", task.getId());
            throw new EmailServiceException("SMTP server temporarily unavailable");
        } else if (scenario == 3) {
            // 10% chance of permanent failure
            logger.error("Permanent failure occurred for task: {}", task.getId());
            throw new PermanentTaskException("Invalid email address format");
        }
        
        // 60% chance of success
        logger.info("Email sent successfully for task: {}", task.getId());
        return TaskResult.success("Email sent successfully");
    }
    
    @Recover
    public TaskResult recoverEmailSend(EmailServiceException ex, Task task) {
        logger.error("All retry attempts exhausted for task: {}. Error: {}", 
                    task.getId(), ex.getMessage());
        
        // Mark for dead letter queue
        return TaskResult.failure("Email delivery failed after all retries - queued for manual review", ex);
    }
}
