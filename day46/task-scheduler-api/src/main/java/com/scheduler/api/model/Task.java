package com.scheduler.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Task name is required")
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @NotBlank(message = "Cron expression is required")
    @Column(nullable = false)
    private String cronExpression;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.ACTIVE;
    
    private Integer maxRetries = 3;
    
    private Integer timeoutSeconds = 300;
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastExecutedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
