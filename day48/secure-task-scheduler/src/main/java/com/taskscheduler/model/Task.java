package com.taskscheduler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String cronExpression;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.INACTIVE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime lastExecuted;
    
    private LocalDateTime nextExecution;
    
    private int executionCount = 0;
    
    public enum TaskStatus {
        ACTIVE, INACTIVE, FAILED, COMPLETED
    }
}
