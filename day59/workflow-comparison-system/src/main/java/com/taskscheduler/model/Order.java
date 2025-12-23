package com.taskscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderId;
    private String customerId;
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private String approach; // "TRADITIONAL" or "TEMPORAL"
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private Integer retryCount;
    
    @Column(length = 2000)
    private String executionHistory;
}
