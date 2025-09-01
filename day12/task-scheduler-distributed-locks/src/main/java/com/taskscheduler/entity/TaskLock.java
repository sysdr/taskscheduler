package com.taskscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task_locks", 
       uniqueConstraints = @UniqueConstraint(columnNames = "lock_key"))
public class TaskLock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_lock_seq")
    @SequenceGenerator(name = "task_lock_seq", sequenceName = "task_lock_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "lock_key", nullable = false, unique = true, length = 255)
    @NotBlank
    private String lockKey;
    
    @Column(name = "owner_instance", nullable = false, length = 255)
    @NotBlank
    private String ownerInstance;
    
    @Column(name = "acquired_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime acquiredAt;
    
    @Column(name = "expires_at", nullable = false)
    @NotNull
    private LocalDateTime expiresAt;
    
    @Column(name = "task_type", length = 100)
    private String taskType;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Version
    private Long version;
    
    @UpdateTimestamp
    private LocalDateTime lastModified;
    
    // Constructors
    public TaskLock() {}
    
    public TaskLock(String lockKey, String ownerInstance, LocalDateTime expiresAt) {
        this.lockKey = lockKey;
        this.ownerInstance = ownerInstance;
        this.expiresAt = expiresAt;
        this.acquiredAt = LocalDateTime.now();
    }
    
    public TaskLock(String lockKey, String ownerInstance, LocalDateTime expiresAt, 
                   String taskType, String description) {
        this(lockKey, ownerInstance, expiresAt);
        this.taskType = taskType;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getLockKey() { return lockKey; }
    public void setLockKey(String lockKey) { this.lockKey = lockKey; }
    
    public String getOwnerInstance() { return ownerInstance; }
    public void setOwnerInstance(String ownerInstance) { this.ownerInstance = ownerInstance; }
    
    public LocalDateTime getAcquiredAt() { return acquiredAt; }
    public void setAcquiredAt(LocalDateTime acquiredAt) { this.acquiredAt = acquiredAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    // Business methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isOwnedBy(String instanceId) {
        return Objects.equals(ownerInstance, instanceId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskLock taskLock = (TaskLock) o;
        return Objects.equals(lockKey, taskLock.lockKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(lockKey);
    }
    
    @Override
    public String toString() {
        return "TaskLock{" +
                "id=" + id +
                ", lockKey='" + lockKey + '\'' +
                ", ownerInstance='" + ownerInstance + '\'' +
                ", acquiredAt=" + acquiredAt +
                ", expiresAt=" + expiresAt +
                ", taskType='" + taskType + '\'' +
                ", expired=" + isExpired() +
                '}';
    }
}
