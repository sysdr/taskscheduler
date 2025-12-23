package com.taskscheduler.temporal;

import com.taskscheduler.model.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TemporalOrderService {
    private static final Logger logger = LoggerFactory.getLogger(TemporalOrderService.class);
    private static final String TASK_QUEUE = "order-processing";

    @Value("${temporal.server.host:localhost}")
    private String temporalHost;

    @Value("${temporal.server.port:7233}")
    private int temporalPort;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderActivitiesImpl orderActivities;

    private WorkflowServiceStubs service;
    private WorkflowClient client;
    private WorkerFactory factory;
    private boolean temporalAvailable = false;

    @PostConstruct
    public void init() {
        try {
            // Connect to Temporal server
            service = WorkflowServiceStubs.newLocalServiceStubs();
            client = WorkflowClient.newInstance(service);
            factory = WorkerFactory.newInstance(client);

            // Create worker
            Worker worker = factory.newWorker(TASK_QUEUE);
            worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
            worker.registerActivitiesImplementations(orderActivities);

            factory.start();
            temporalAvailable = true;
            logger.info("Temporal worker started successfully");
        } catch (Exception e) {
            logger.warn("Temporal server not available. Running in demo mode: {}", e.getMessage());
            temporalAvailable = false;
        }
    }

    public Order createOrder(String customerId, Double amount) {
        Order order = new Order();
        order.setOrderId("TMP-" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setAmount(amount);
        order.setStatus(OrderStatus.CREATED);
        order.setApproach("TEMPORAL");
        order.setCreatedAt(LocalDateTime.now());
        order.setRetryCount(0);
        order.setExecutionHistory("Order created (Temporal workflow)");

        order = orderRepository.save(order);

        if (temporalAvailable) {
            startWorkflow(order);
        } else {
            // Simulate workflow in demo mode
            simulateWorkflowInDemoMode(order);
        }

        return order;
    }

    private void startWorkflow(Order order) {
        try {
            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue(TASK_QUEUE)
                    .setWorkflowId(order.getOrderId())
                    .build();

            OrderWorkflow workflow = client.newWorkflowStub(OrderWorkflow.class, options);
            
            // Start workflow asynchronously
            WorkflowClient.start(workflow::processOrder, 
                    order.getOrderId(), order.getCustomerId(), order.getAmount());
            
            logger.info("Temporal workflow started for order: {}", order.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to start workflow: {}", e.getMessage());
            order.setStatus(OrderStatus.FAILED);
            order.setErrorMessage(e.getMessage());
            orderRepository.save(order);
        }
    }

    private void simulateWorkflowInDemoMode(Order order) {
        // Simulate workflow execution in background thread
        new Thread(() -> {
            try {
                Thread.sleep(500);
                orderActivities.validateOrder(order.getOrderId());
                
                Thread.sleep(800);
                orderActivities.chargePayment(order.getOrderId(), order.getAmount());
                
                Thread.sleep(1000);
                orderActivities.fulfillOrder(order.getOrderId());
                
                Thread.sleep(300);
                orderActivities.sendNotification(order.getOrderId(), order.getCustomerId());
                
                logger.info("Demo workflow completed for order: {}", order.getOrderId());
            } catch (Exception e) {
                logger.error("Demo workflow failed: {}", e.getMessage());
            }
        }).start();
    }

    @PreDestroy
    public void shutdown() {
        if (factory != null) {
            factory.shutdown();
        }
        if (service != null) {
            service.shutdown();
        }
    }

    public boolean isTemporalAvailable() {
        return temporalAvailable;
    }
}
