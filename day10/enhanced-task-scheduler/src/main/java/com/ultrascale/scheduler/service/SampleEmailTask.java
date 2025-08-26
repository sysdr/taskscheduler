package com.ultrascale.scheduler.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SampleEmailTask {
    
    public String sendEmail(String recipient, String subject, String content) {
        // Simulate email sending
        try {
            Thread.sleep(1000); // Simulate network delay
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String emailContent = String.format(
                "Email sent successfully:\n" +
                "To: %s\n" +
                "Subject: %s\n" +
                "Content: %s\n" +
                "Timestamp: %s",
                recipient, subject, content, timestamp
            );
            
            return emailContent;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email sending interrupted", e);
        }
    }
    
    public String getTaskDescription() {
        return "Simulates sending emails with configurable recipient, subject, and content";
    }
}
