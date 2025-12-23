package com.taskscheduler.temporal;

import com.taskscheduler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class OrderActivitiesImpl implements OrderActivities {
    private static final Logger logger = LoggerFactory.getLogger(OrderActivitiesImpl.class);
    private final Random random = new Random();

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void validateOrder(String orderId) {
        logger.info("Validating order: {}", orderId);
        updateOrderStatus(orderId, OrderStatus.VALIDATING);
        
        simulateWork(500);
        
        if (shouldSimulateFailure()) {
            throw new RuntimeException("Validation service unavailable");
        }
        
        updateOrderStatus(orderId, OrderStatus.VALIDATED);
        appendHistory(orderId, "Order validated");
    }

    @Override
    public void chargePayment(String orderId, Double amount) {
        logger.info("Charging payment for order: {}", orderId);
        updateOrderStatus(orderId, OrderStatus.CHARGING);
        
        simulateWork(800);
        
        if (shouldSimulateFailure()) {
            throw new RuntimeException("Payment gateway timeout");
        }
        
        updateOrderStatus(orderId, OrderStatus.CHARGED);
        appendHistory(orderId, "Payment charged: $" + amount);
    }

    @Override
    public void fulfillOrder(String orderId) {
        logger.info("Fulfilling order: {}", orderId);
        updateOrderStatus(orderId, OrderStatus.FULFILLING);
        
        simulateWork(1000);
        
        if (shouldSimulateFailure()) {
            throw new RuntimeException("Inventory service error");
        }
        
        updateOrderStatus(orderId, OrderStatus.FULFILLED);
        appendHistory(orderId, "Order fulfilled");
    }

    @Override
    public void sendNotification(String orderId, String customerId) {
        logger.info("Sending notification for order: {}", orderId);
        updateOrderStatus(orderId, OrderStatus.NOTIFYING);
        
        simulateWork(300);
        
        updateOrderStatus(orderId, OrderStatus.COMPLETED);
        Order order = orderRepository.findByOrderId(orderId).orElseThrow();
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
        
        appendHistory(orderId, "Customer notified");
    }

    private void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
    }

    private void appendHistory(String orderId, String message) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow();
        String current = order.getExecutionHistory() != null ? order.getExecutionHistory() : "";
        order.setExecutionHistory(current + "\n" + LocalDateTime.now() + ": " + message);
        orderRepository.save(order);
    }

    private void simulateWork(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean shouldSimulateFailure() {
        return random.nextInt(100) < 15; // 15% failure rate
    }
}
