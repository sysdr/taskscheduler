package com.scheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task_definitions")
public class TaskDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String cronExpression;
    
    @Column(nullable = false)
    private String taskClass;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.ACTIVE;
    
    @Column
    private String parameters;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime lastExecutedAt;
    
    @Column
    private LocalDateTime nextExecutionAt;
    
    @Version
    private Long version;
    
    // Constructors
    public TaskDefinition() {}
    
    public TaskDefinition(String name, String description, String cronExpression, String taskClass) {
        this.name = name;
        this.description = description;
        this.cronExpression = cronExpression;
        this.taskClass = taskClass;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    
    public String getTaskClass() { return taskClass; }
    public void setTaskClass(String taskClass) { this.taskClass = taskClass; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastExecutedAt() { return lastExecutedAt; }
    public void setLastExecutedAt(LocalDateTime lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; }
    
    public LocalDateTime getNextExecutionAt() { return nextExecutionAt; }
    public void setNextExecutionAt(LocalDateTime nextExecutionAt) { this.nextExecutionAt = nextExecutionAt; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
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
        return String.format("TaskDefinition{name='%s', status=%s, cron='%s'}", 
                           name, status, cronExpression);
    }
}
