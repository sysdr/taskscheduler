package com.taskscheduler.controller;

import com.taskscheduler.model.*;
import com.taskscheduler.traditional.TraditionalOrderProcessor;
import com.taskscheduler.temporal.TemporalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkflowController {

    @Autowired
    private TraditionalOrderProcessor traditionalProcessor;

    @Autowired
    private TemporalOrderService temporalService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/orders/traditional")
    public ResponseEntity<Order> createTraditionalOrder(@RequestBody OrderRequest request) {
        Order order = traditionalProcessor.createOrder(request.getCustomerId(), request.getAmount());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/orders/temporal")
    public ResponseEntity<Order> createTemporalOrder(@RequestBody OrderRequest request) {
        Order order = temporalService.createOrder(request.getCustomerId(), request.getAmount());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<Order> allOrders = orderRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", allOrders.size());
        stats.put("traditionalOrders", allOrders.stream().filter(o -> "TRADITIONAL".equals(o.getApproach())).count());
        stats.put("temporalOrders", allOrders.stream().filter(o -> "TEMPORAL".equals(o.getApproach())).count());
        stats.put("completedOrders", allOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count());
        stats.put("failedOrders", allOrders.stream().filter(o -> o.getStatus() == OrderStatus.FAILED).count());
        stats.put("temporalAvailable", temporalService.isTemporalAvailable());
        
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/orders/reset")
    public ResponseEntity<Void> resetOrders() {
        orderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}

class OrderRequest {
    private String customerId;
    private Double amount;

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
