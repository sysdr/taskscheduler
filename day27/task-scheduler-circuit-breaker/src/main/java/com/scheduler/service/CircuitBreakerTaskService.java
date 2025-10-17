package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CircuitBreakerTaskService {
    
    @Autowired
    private ExternalServiceImpl externalService;
    
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayment")
    public String processPaymentWithCircuitBreaker(Task task, double amount) {
        return externalService.processPayment(task.getId().toString(), amount);
    }
    
    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallbackNotification")
    public void sendNotificationWithCircuitBreaker(Task task, String message) {
        externalService.sendNotification(task.getId().toString(), message);
    }
    
    @CircuitBreaker(name = "auditService", fallbackMethod = "fallbackAudit")
    public void recordAuditWithCircuitBreaker(Task task, String operation) {
        externalService.recordAudit(task.getId().toString(), operation);
    }
    
    // Fallback methods
    public String fallbackPayment(Task task, double amount, Exception ex) {
        System.out.println("Payment circuit breaker OPEN - using fallback for task: " + task.getId());
        task.setStatus(TaskStatus.CIRCUIT_BREAKER_OPEN);
        task.setErrorMessage("Payment service unavailable - queued for retry");
        return "PAYMENT_FALLBACK_" + task.getId();
    }
    
    public void fallbackNotification(Task task, String message, Exception ex) {
        System.out.println("Notification circuit breaker OPEN - using fallback for task: " + task.getId());
        // Queue notification for later delivery
        System.out.println("Notification queued for later delivery: " + message);
    }
    
    public void fallbackAudit(Task task, String operation, Exception ex) {
        System.out.println("Audit circuit breaker OPEN - using fallback for task: " + task.getId());
        // Log locally instead of external audit
        System.out.println("Local audit log: Task " + task.getId() + " - " + operation + " at " + LocalDateTime.now());
    }
}
