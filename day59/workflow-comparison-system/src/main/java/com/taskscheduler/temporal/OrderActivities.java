package com.taskscheduler.temporal;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {
    void validateOrder(String orderId);
    void chargePayment(String orderId, Double amount);
    void fulfillOrder(String orderId);
    void sendNotification(String orderId, String customerId);
}
