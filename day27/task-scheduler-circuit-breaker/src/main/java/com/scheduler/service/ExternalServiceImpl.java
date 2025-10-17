package com.scheduler.service;

import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExternalServiceImpl implements ExternalService {
    
    private final Random random = new Random();
    private volatile boolean paymentServiceDown = false;
    private volatile boolean notificationServiceFlakey = false;
    
    @Override
    public String processPayment(String taskId, double amount) {
        simulateLatency(2000, 5000);
        
        if (paymentServiceDown || random.nextDouble() < 0.3) {
            throw new RuntimeException("Payment service unavailable");
        }
        
        return "PAYMENT_" + taskId + "_SUCCESS";
    }
    
    @Override
    public void sendNotification(String taskId, String message) {
        simulateLatency(500, 1500);
        
        if (notificationServiceFlakey && random.nextDouble() < 0.4) {
            throw new RuntimeException("Notification service temporarily unavailable");
        }
        
        System.out.println("Notification sent for task: " + taskId + " - " + message);
    }
    
    @Override
    public void recordAudit(String taskId, String operation) {
        simulateLatency(1000, 3000);
        
        if (random.nextDouble() < 0.1) {
            throw new RuntimeException("Audit service timeout");
        }
        
        System.out.println("Audit recorded: Task " + taskId + " - " + operation);
    }
    
    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Control methods for demo
    public void setPaymentServiceDown(boolean down) {
        this.paymentServiceDown = down;
    }
    
    public void setNotificationServiceFlakey(boolean flakey) {
        this.notificationServiceFlakey = flakey;
    }
    
    public boolean isPaymentServiceDown() {
        return paymentServiceDown;
    }
    
    public boolean isNotificationServiceFlakey() {
        return notificationServiceFlakey;
    }
}
