package com.payment.controller;

import com.payment.service.PaymentSchedulerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private PaymentSchedulerClient schedulerClient;
    
    @PostMapping("/reconcile")
    public Map<String, Object> scheduleReconciliation() {
        return schedulerClient.submitTask(
            "payment-reconciliation",
            "{\"type\": \"daily-reconciliation\", \"batch\": \"2024-01-15\"}",
            "http://localhost:8081/api/payments/callback"
        );
    }
    
    @PostMapping("/callback")
    public void handleCallback(@RequestBody Map<String, Object> result) {
        System.out.println("Payment task completed: " + result);
    }
}
