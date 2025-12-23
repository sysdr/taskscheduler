package com.taskscheduler.traditional;

import com.taskscheduler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class TraditionalOrderProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TraditionalOrderProcessor.class);
    private static final int MAX_RETRIES = 3;
    private final Random random = new Random();

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(String customerId, Double amount) {
        Order order = new Order();
        order.setOrderId("TRD-" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setAmount(amount);
        order.setStatus(OrderStatus.CREATED);
        order.setApproach("TRADITIONAL");
        order.setCreatedAt(LocalDateTime.now());
        order.setRetryCount(0);
        order.setExecutionHistory("Order created");
        
        order = orderRepository.save(order);
        logger.info("Traditional order created: {}", order.getOrderId());
        return order;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOrders() {
        List<Order> pendingOrders = orderRepository.findByApproach("TRADITIONAL")
                .stream()
                .filter(o -> o.getStatus() != OrderStatus.COMPLETED && 
                            o.getStatus() != OrderStatus.FAILED)
                .toList();

        for (Order order : pendingOrders) {
            try {
                processOrderStep(order);
            } catch (Exception e) {
                handleError(order, e);
            }
        }
    }

    private void processOrderStep(Order order) throws Exception {
        switch (order.getStatus()) {
            case CREATED -> validateOrder(order);
            case VALIDATED -> chargePayment(order);
            case CHARGED -> fulfillOrder(order);
            case FULFILLED -> sendNotification(order);
            case NOTIFYING -> completeOrder(order);
        }
    }

    private void validateOrder(Order order) throws Exception {
        logger.info("Validating order: {}", order.getOrderId());
        order.setStatus(OrderStatus.VALIDATING);
        orderRepository.save(order);
        
        simulateWork(500);
        
        if (shouldSimulateFailure()) {
            throw new RuntimeException("Validation service unavailable");
        }
        
        order.setStatus(OrderStatus.VALIDATED);
        appendHistory(order, "Order validated");
        orderRepository.save(order);
    }

    private void chargePayment(Order order) throws Exception {
        logger.info("Charging payment: {}", order.getOrderId());
        order.setStatus(OrderStatus.CHARGING);
        orderRepository.save(order);
        
        simulateWork(800);
        
        if (shouldSimulateFailure()) {
            throw new RuntimeException("Payment gateway timeout");
        }
        
        order.setStatus(OrderStatus.CHARGED);
        appendHistory(order, "Payment charged: $" + order.getAmount());
        orderRepository.save(order);
    }

    private void fulfillOrder(Order order) throws Exception {
        logger.info("Fulfilling order: {}", order.getOrderId());
        order.setStatus(OrderStatus.FULFILLING);
        orderRepository.save(order);
        
        simulateWork(1000);
        
        if (shouldSimulateFailure()) {
            throw new RuntimeException("Inventory service error");
        }
        
        order.setStatus(OrderStatus.FULFILLED);
        appendHistory(order, "Order fulfilled");
        orderRepository.save(order);
    }

    private void sendNotification(Order order) throws Exception {
        logger.info("Sending notification: {}", order.getOrderId());
        order.setStatus(OrderStatus.NOTIFYING);
        orderRepository.save(order);
        
        simulateWork(300);
        
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        appendHistory(order, "Customer notified");
        orderRepository.save(order);
    }

    private void completeOrder(Order order) {
        logger.info("Order completed: {}", order.getOrderId());
    }

    private void handleError(Order order, Exception e) {
        logger.error("Error processing order {}: {}", order.getOrderId(), e.getMessage());
        
        order.setRetryCount(order.getRetryCount() + 1);
        order.setErrorMessage(e.getMessage());
        appendHistory(order, "Error: " + e.getMessage() + " (Retry " + order.getRetryCount() + ")");
        
        if (order.getRetryCount() >= MAX_RETRIES) {
            order.setStatus(OrderStatus.FAILED);
            appendHistory(order, "Failed after " + MAX_RETRIES + " retries");
            logger.error("Order failed after max retries: {}", order.getOrderId());
        }
        
        orderRepository.save(order);
    }

    private void appendHistory(Order order, String message) {
        String current = order.getExecutionHistory() != null ? order.getExecutionHistory() : "";
        order.setExecutionHistory(current + "\n" + LocalDateTime.now() + ": " + message);
    }

    private void simulateWork(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    private boolean shouldSimulateFailure() {
        // 15% chance of failure to demonstrate retry logic
        return random.nextInt(100) < 15;
    }
}
