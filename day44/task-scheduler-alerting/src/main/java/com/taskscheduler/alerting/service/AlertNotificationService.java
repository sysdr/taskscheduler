package com.taskscheduler.alerting.service;

import com.taskscheduler.alerting.model.Alert;
import com.taskscheduler.alerting.model.AlertSeverity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AlertNotificationService {
    
    private final JavaMailSender mailSender;
    private final WebClient webClient;
    
    @Value("${alerting.slack.webhook-url}")
    private String slackWebhookUrl;
    
    @Value("${alerting.slack.enabled}")
    private boolean slackEnabled;
    
    @Value("${alerting.email.enabled}")
    private boolean emailEnabled;
    
    @Value("${alerting.email.recipients}")
    private String emailRecipients;
    
    @Value("${alerting.webhook.enabled}")
    private boolean webhookEnabled;
    
    @Value("${alerting.webhook.url}")
    private String webhookUrl;
    
    public AlertNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.webClient = WebClient.builder().build();
    }
    
    public void sendAlert(Alert alert) {
        log.info("Sending alert: {} (Severity: {})", alert.getName(), alert.getSeverity());
        
        // Send to appropriate channels based on severity and status
        CompletableFuture.runAsync(() -> {
            if (slackEnabled) {
                sendSlackNotification(alert);
            }
            
            if (emailEnabled && (alert.getSeverity() == AlertSeverity.CRITICAL || 
                                 alert.getSeverity() == AlertSeverity.HIGH)) {
                sendEmailNotification(alert);
            }
            
            if (webhookEnabled) {
                sendWebhookNotification(alert);
            }
        });
    }
    
    private void sendSlackNotification(Alert alert) {
        try {
            String color = getSlackColor(alert.getSeverity());
            String emoji = getAlertEmoji(alert.getSeverity());
            String statusText = alert.getStatus().toString();
            
            Map<String, Object> slackMessage = new HashMap<>();
            slackMessage.put("text", String.format("%s Alert: %s", emoji, alert.getName()));
            
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("color", color);
            attachment.put("title", alert.getName());
            attachment.put("text", alert.getDescription());
            attachment.put("footer", String.format("Status: %s | Metric: %s | Current: %.2f | Threshold: %.2f", 
                statusText, alert.getMetric(), alert.getCurrentValue(), alert.getThreshold()));
            
            Map<String, String>[] fields = new Map[2];
            fields[0] = Map.of("title", "Runbook", "value", alert.getRunbookUrl(), "short", "true");
            fields[1] = Map.of("title", "Dashboard", "value", alert.getDashboardUrl(), "short", "true");
            attachment.put("fields", fields);
            
            slackMessage.put("attachments", new Object[]{attachment});
            
            webClient.post()
                .uri(slackWebhookUrl)
                .bodyValue(slackMessage)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> log.info("Slack notification sent for alert: {}", alert.getName()),
                    error -> log.error("Failed to send Slack notification: {}", error.getMessage())
                );
            
        } catch (Exception e) {
            log.error("Error sending Slack notification", e);
        }
    }
    
    private void sendEmailNotification(Alert alert) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailRecipients.split(","));
            message.setSubject(String.format("[%s] %s", alert.getSeverity(), alert.getName()));
            message.setText(buildEmailBody(alert));
            
            mailSender.send(message);
            log.info("Email notification sent for alert: {}", alert.getName());
            
        } catch (Exception e) {
            log.error("Error sending email notification", e);
        }
    }
    
    private void sendWebhookNotification(Alert alert) {
        try {
            webClient.post()
                .uri(webhookUrl)
                .bodyValue(alert)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> log.info("Webhook notification sent for alert: {}", alert.getName()),
                    error -> log.error("Failed to send webhook notification: {}", error.getMessage())
                );
        } catch (Exception e) {
            log.error("Error sending webhook notification", e);
        }
    }
    
    private String getSlackColor(AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "#ff0000";  // Red
            case HIGH -> "#ff9900";       // Orange
            case MEDIUM -> "#ffcc00";     // Yellow
            case LOW -> "#36a64f";        // Green
        };
    }
    
    private String getAlertEmoji(AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "🚨";
            case HIGH -> "⚠️";
            case MEDIUM -> "⚡";
            case LOW -> "ℹ️";
        };
    }
    
    private String buildEmailBody(Alert alert) {
        StringBuilder body = new StringBuilder();
        body.append("Alert Details:\n\n");
        body.append(String.format("Name: %s\n", alert.getName()));
        body.append(String.format("Severity: %s\n", alert.getSeverity()));
        body.append(String.format("Status: %s\n", alert.getStatus()));
        body.append(String.format("Description: %s\n\n", alert.getDescription()));
        body.append(String.format("Metric: %s\n", alert.getMetric()));
        body.append(String.format("Current Value: %.2f\n", alert.getCurrentValue()));
        body.append(String.format("Threshold: %.2f\n\n", alert.getThreshold()));
        body.append(String.format("Triggered At: %s\n", alert.getTriggeredAt()));
        body.append(String.format("\nRunbook: %s\n", alert.getRunbookUrl()));
        body.append(String.format("Dashboard: %s\n", alert.getDashboardUrl()));
        
        return body.toString();
    }
}
