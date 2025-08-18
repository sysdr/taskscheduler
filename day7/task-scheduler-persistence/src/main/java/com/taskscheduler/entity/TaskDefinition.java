package com.taskscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task_definitions", 
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class TaskDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_definition_seq", allocationSize = 1)
    private Long id;
    
    @NotBlank(message = "Task name cannot be blank")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @NotBlank(message = "Task type cannot be blank")
    @Column(nullable = false, length = 50)
    private String type;
    
    @NotBlank(message = "Schedule expression cannot be blank")
    @Column(nullable = false, length = 100)
    private String scheduleExpression;
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.INACTIVE;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
    
    // Constructors
    public TaskDefinition() {}
    
    public TaskDefinition(String name, String type, String scheduleExpression) {
        this.name = name;
        this.type = type;
        this.scheduleExpression = scheduleExpression;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getScheduleExpression() { return scheduleExpression; }
    public void setScheduleExpression(String scheduleExpression) { 
        this.scheduleExpression = scheduleExpression; 
    }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    // Utility methods
    public void activate() { this.status = TaskStatus.ACTIVE; }
    public void deactivate() { this.status = TaskStatus.INACTIVE; }
    public boolean isActive() { return TaskStatus.ACTIVE.equals(this.status); }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskDefinition)) return false;
        TaskDefinition that = (TaskDefinition) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "TaskDefinition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", enabled=" + enabled +
                '}';
    }
    
    public enum TaskStatus {
        ACTIVE, INACTIVE, PAUSED, ERROR
    }
}
