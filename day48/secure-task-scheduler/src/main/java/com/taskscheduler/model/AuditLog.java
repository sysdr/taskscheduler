package com.taskscheduler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    private String action;
    
    private String resourceType;
    
    private Long resourceId;
    
    private String ipAddress;
    
    @Column(length = 2000)
    private String details;
    
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    private ActionResult result;
    
    public enum ActionResult {
        SUCCESS, FAILURE, UNAUTHORIZED
    }
}
