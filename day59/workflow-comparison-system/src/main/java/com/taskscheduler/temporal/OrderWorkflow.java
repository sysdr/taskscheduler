package com.taskscheduler.temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod
    String processOrder(String orderId, String customerId, Double amount);
}
