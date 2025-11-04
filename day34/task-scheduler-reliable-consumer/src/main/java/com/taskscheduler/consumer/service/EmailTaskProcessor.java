package com.taskscheduler.consumer.service;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class EmailTaskProcessor implements TaskProcessor {
    private static final Logger log = LoggerFactory.getLogger(EmailTaskProcessor.class);
    private final Random random = new Random();

    @Override
    public ProcessingResult process(Task task) {
        long startTime = System.currentTimeMillis();
        
        try {
            String recipient = (String) task.getPayload().get("recipient");
            String subject = (String) task.getPayload().get("subject");
            
            if (recipient == null || recipient.trim().isEmpty()) {
                return ProcessingResult.permanentFailure(
                    "Invalid recipient email", 
                    new IllegalArgumentException("Recipient cannot be null or empty"),
                    System.currentTimeMillis() - startTime
                );
            }

            // Simulate processing time
            Thread.sleep(100 + random.nextInt(200));
            
            // Simulate different failure scenarios
            int scenario = random.nextInt(100);
            if (scenario < 5) { // 5% network timeout
                throw new RuntimeException("Network timeout - connection refused");
            } else if (scenario < 8) { // 3% service unavailable
                throw new RuntimeException("Email service temporarily unavailable");
            } else if (scenario < 10) { // 2% invalid format
                return ProcessingResult.permanentFailure(
                    "Invalid email format: " + recipient,
                    new IllegalArgumentException("Invalid email format"),
                    System.currentTimeMillis() - startTime
                );
            }
            
            log.info("✅ Email sent successfully to {} with subject: {}", recipient, subject);
            return ProcessingResult.success(
                "Email sent to " + recipient, 
                System.currentTimeMillis() - startTime
            );
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ProcessingResult.retryableFailure(
                "Processing interrupted", e, 
                System.currentTimeMillis() - startTime
            );
        } catch (Exception e) {
            if (e.getMessage().contains("timeout") || e.getMessage().contains("unavailable")) {
                return ProcessingResult.retryableFailure(
                    "Transient failure: " + e.getMessage(), e,
                    System.currentTimeMillis() - startTime
                );
            }
            return ProcessingResult.permanentFailure(
                "Unexpected error: " + e.getMessage(), e,
                System.currentTimeMillis() - startTime
            );
        }
    }

    @Override
    public boolean canProcess(String taskType) {
        return "EMAIL".equalsIgnoreCase(taskType);
    }
}
