package com.taskscheduler.service;

import com.taskscheduler.component.IdempotentTaskWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EmailNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    
    @Autowired
    private IdempotentTaskWrapper taskWrapper;

    /**
     * Simulates sending a welcome email with idempotency
     */
    public String sendWelcomeEmail(String userEmail, String userName) {
        return taskWrapper.executeIdempotent(
            "send_welcome_email",
            () -> {
                // Simulate email sending delay
                simulateEmailSending();
                
                String message = String.format("Welcome email sent to %s (%s) at %s", 
                                               userEmail, userName, LocalDateTime.now());
                logger.info(message);
                return message;
            },
            userEmail, userName
        );
    }

    /**
     * Simulates sending a password reset email with idempotency
     */
    public String sendPasswordResetEmail(String userEmail, String resetToken) {
        return taskWrapper.executeIdempotent(
            "send_password_reset",
            () -> {
                simulateEmailSending();
                
                String message = String.format("Password reset email sent to %s with token %s at %s", 
                                               userEmail, resetToken, LocalDateTime.now());
                logger.info(message);
                return message;
            },
            userEmail, resetToken
        );
    }

    /**
     * Scheduled task that demonstrates idempotent execution
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void processEmailQueue() {
        taskWrapper.executeIdempotentVoid(
            "process_email_queue",
            () -> {
                logger.info("Processing email queue at {}", LocalDateTime.now());
                
                // Simulate processing multiple emails
                int emailCount = ThreadLocalRandom.current().nextInt(1, 6);
                for (int i = 0; i < emailCount; i++) {
                    simulateEmailSending();
                }
                
                logger.info("Processed {} emails from queue", emailCount);
            },
            LocalDateTime.now().getMinute() // Use minute as parameter for idempotency
        );
    }

    private void simulateEmailSending() {
        try {
            // Simulate email service call delay
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 500));
            
            // Simulate occasional failures
            if (ThreadLocalRandom.current().nextDouble() < 0.1) {
                throw new RuntimeException("Simulated email service timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email sending interrupted", e);
        }
    }
}
