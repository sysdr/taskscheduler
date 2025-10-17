package com.scheduler.service;

public interface ExternalService {
    String processPayment(String taskId, double amount);
    void sendNotification(String taskId, String message);
    void recordAudit(String taskId, String operation);
}
