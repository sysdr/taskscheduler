package com.taskscheduler.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class OrderWorkflowImpl implements OrderWorkflow {
    private static final Logger logger = Workflow.getLogger(OrderWorkflowImpl.class);

    private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(3)
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setMaximumInterval(Duration.ofSeconds(10))
                    .setBackoffCoefficient(2.0)
                    .build())
            .build();

    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class, activityOptions);

    @Override
    public String processOrder(String orderId, String customerId, Double amount) {
        logger.info("Starting Temporal workflow for order: {}", orderId);

        try {
            // Step 1: Validate
            activities.validateOrder(orderId);
            logger.info("Order validated: {}", orderId);

            // Step 2: Charge
            activities.chargePayment(orderId, amount);
            logger.info("Payment charged: {}", orderId);

            // Step 3: Fulfill
            activities.fulfillOrder(orderId);
            logger.info("Order fulfilled: {}", orderId);

            // Step 4: Notify
            activities.sendNotification(orderId, customerId);
            logger.info("Notification sent: {}", orderId);

            return "Order completed successfully: " + orderId;
        } catch (Exception e) {
            logger.error("Workflow failed for order {}: {}", orderId, e.getMessage());
            // Temporal handles compensation automatically via activity retries
            throw e;
        }
    }
}
