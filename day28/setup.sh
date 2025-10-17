#!/bin/bash

# Day 28: Async Task Execution Implementation Script
# Creates complete project structure with real async task scheduler

set -e

PROJECT_NAME="async-task-scheduler"
PROJECT_DIR="$PWD/$PROJECT_NAME"

echo "🚀 Creating Day 28: Async Task Scheduler Project..."

# Create project structure
mkdir -p "$PROJECT_DIR"
cd "$PROJECT_DIR"

# Create directory structure
mkdir -p {src/main/{java/com/scheduler/{config,service,controller,model,repository},resources/{static/{css,js},templates}},src/test/java/com/scheduler}

echo "📁 Created project directory structure"

# Create build.gradle
cat > build.gradle << 'EOF'
plugins {
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'java'
}

group = 'com.scheduler'
version = '1.0.0'
sourceCompatibility = '21'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'com.h2database:h2'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
EOF

# Create application.properties
cat > src/main/resources/application.properties << 'EOF'
# Server Configuration
server.port=8080
spring.application.name=async-task-scheduler

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Async Configuration
spring.task.execution.pool.core-size=5
spring.task.execution.pool.max-size=20
spring.task.execution.pool.queue-capacity=100
spring.task.execution.thread-name-prefix=async-task-

# Actuator Configuration
management.endpoints.web.exposure.include=health,metrics,info,threaddump
management.endpoint.health.show-details=always

# Logging
logging.level.com.scheduler=INFO
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
EOF

# Create Main Application Class
cat > src/main/java/com/scheduler/AsyncTaskSchedulerApplication.java << 'EOF'
package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AsyncTaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsyncTaskSchedulerApplication.class, args);
    }
}
EOF

# Create Task Model
cat > src/main/java/com/scheduler/model/Task.java << 'EOF'
package com.scheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String type;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String result;
    private String errorMessage;
    private Integer processingTimeSeconds;
    
    @Transient
    private Future<String> futureResult;
    
    public Task() {
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Task(String name, String type, String description, Integer processingTimeSeconds) {
        this();
        this.name = name;
        this.type = type;
        this.description = description;
        this.processingTimeSeconds = processingTimeSeconds;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getProcessingTimeSeconds() { return processingTimeSeconds; }
    public void setProcessingTimeSeconds(Integer processingTimeSeconds) { this.processingTimeSeconds = processingTimeSeconds; }
    
    public Future<String> getFutureResult() { return futureResult; }
    public void setFutureResult(Future<String> futureResult) { this.futureResult = futureResult; }
}
EOF

# Create TaskStatus Enum
cat > src/main/java/com/scheduler/model/TaskStatus.java << 'EOF'
package com.scheduler.model;

public enum TaskStatus {
    PENDING,
    SUBMITTED,
    EXECUTING,
    COMPLETED,
    FAILED,
    CANCELLED
}
EOF

# Create Task Repository
cat > src/main/java/com/scheduler/repository/TaskRepository.java << 'EOF'
package com.scheduler.repository;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByTypeOrderByCreatedAtDesc(String type);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(TaskStatus status);
    
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findAllOrderByCreatedAtDesc();
}
EOF

# Create Async Configuration
cat > src/main/java/com/scheduler/config/AsyncConfig.java << 'EOF'
package com.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Email-Task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "reportTaskExecutor")
    public Executor reportTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Report-Task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "dataTaskExecutor")
    public Executor dataTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Data-Task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
EOF

# Create Async Task Service
cat > src/main/java/com/scheduler/service/AsyncTaskService.java << 'EOF'
package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AsyncTaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Async("emailTaskExecutor")
    public CompletableFuture<String> processEmailTask(Task task) {
        return processTaskAsync(task, "Email");
    }
    
    @Async("reportTaskExecutor")
    public CompletableFuture<String> processReportTask(Task task) {
        return processTaskAsync(task, "Report");
    }
    
    @Async("dataTaskExecutor")
    public CompletableFuture<String> processDataTask(Task task) {
        return processTaskAsync(task, "Data");
    }
    
    private CompletableFuture<String> processTaskAsync(Task task, String taskTypeLabel) {
        String threadName = Thread.currentThread().getName();
        logger.info("Starting {} task '{}' on thread: {}", taskTypeLabel, task.getName(), threadName);
        
        try {
            // Update task status to executing
            task.setStatus(TaskStatus.EXECUTING);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            // Simulate processing time
            int processingTime = task.getProcessingTimeSeconds() != null ? 
                task.getProcessingTimeSeconds() : ThreadLocalRandom.current().nextInt(3, 8);
            
            Thread.sleep(processingTime * 1000);
            
            // Simulate random success/failure
            if (ThreadLocalRandom.current().nextDouble() < 0.15) {
                throw new RuntimeException("Simulated processing error");
            }
            
            // Complete successfully
            String result = String.format("%s task '%s' completed successfully in %d seconds on thread %s", 
                taskTypeLabel, task.getName(), processingTime, threadName);
            
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            task.setResult(result);
            taskRepository.save(task);
            
            logger.info("Completed {} task '{}' on thread: {}", taskTypeLabel, task.getName(), threadName);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            logger.error("Failed {} task '{}' on thread: {}", taskTypeLabel, task.getName(), threadName, e);
            
            task.setStatus(TaskStatus.FAILED);
            task.setCompletedAt(LocalDateTime.now());
            task.setErrorMessage(e.getMessage());
            taskRepository.save(task);
            
            return CompletableFuture.failedFuture(e);
        }
    }
}
EOF

# Create Task Service
cat > src/main/java/com/scheduler/service/TaskService.java << 'EOF'
package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private AsyncTaskService asyncTaskService;
    
    public Task submitTask(Task task) {
        // Save task as pending
        task.setStatus(TaskStatus.PENDING);
        Task savedTask = taskRepository.save(task);
        
        // Submit for async processing
        CompletableFuture<String> future = submitTaskForAsyncProcessing(savedTask);
        savedTask.setFutureResult(future);
        
        // Update status to submitted
        savedTask.setStatus(TaskStatus.SUBMITTED);
        taskRepository.save(savedTask);
        
        logger.info("Submitted task '{}' of type '{}' for async processing", 
            savedTask.getName(), savedTask.getType());
        
        return savedTask;
    }
    
    private CompletableFuture<String> submitTaskForAsyncProcessing(Task task) {
        return switch (task.getType().toLowerCase()) {
            case "email" -> asyncTaskService.processEmailTask(task);
            case "report" -> asyncTaskService.processReportTask(task);
            case "data" -> asyncTaskService.processDataTask(task);
            default -> asyncTaskService.processDataTask(task); // Default fallback
        };
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    public List<Task> getTasksByType(String type) {
        return taskRepository.findByTypeOrderByCreatedAtDesc(type);
    }
    
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
}
EOF

# Create Task Controller
cat > src/main/java/com/scheduler/controller/TaskController.java << 'EOF'
package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.submitTask(task);
        return ResponseEntity.ok(createdTask);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Task>> getTasksByType(@PathVariable String type) {
        List<Task> tasks = taskService.getTasksByType(type);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/sample/{type}")
    public ResponseEntity<Task> createSampleTask(@PathVariable String type) {
        String[] emailTasks = {"Welcome Email", "Newsletter", "Password Reset", "Account Verification"};
        String[] reportTasks = {"Monthly Report", "Sales Analysis", "User Analytics", "Performance Report"};
        String[] dataTasks = {"Data Backup", "CSV Import", "Database Cleanup", "Data Sync"};
        
        String[] tasks = switch (type.toLowerCase()) {
            case "email" -> emailTasks;
            case "report" -> reportTasks;
            case "data" -> dataTasks;
            default -> dataTasks;
        };
        
        String taskName = tasks[ThreadLocalRandom.current().nextInt(tasks.length)];
        String description = String.format("Sample %s task for demonstration", type);
        Integer processingTime = ThreadLocalRandom.current().nextInt(3, 10);
        
        Task task = new Task(taskName, type, description, processingTime);
        Task createdTask = taskService.submitTask(task);
        return ResponseEntity.ok(createdTask);
    }
}
EOF

# Create Web Controller for Dashboard
cat > src/main/java/com/scheduler/controller/DashboardController.java << 'EOF'
package com.scheduler.controller;

import com.scheduler.model.TaskStatus;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalTasks", taskService.getAllTasks().size());
        model.addAttribute("pendingTasks", taskService.getTaskCountByStatus(TaskStatus.PENDING));
        model.addAttribute("executingTasks", taskService.getTaskCountByStatus(TaskStatus.EXECUTING));
        model.addAttribute("completedTasks", taskService.getTaskCountByStatus(TaskStatus.COMPLETED));
        model.addAttribute("failedTasks", taskService.getTaskCountByStatus(TaskStatus.FAILED));
        return "dashboard";
    }
}
EOF

# Create CSS
cat > src/main/resources/static/css/dashboard.css << 'EOF'
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    color: #333;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.header {
    text-align: center;
    margin-bottom: 40px;
    color: white;
}

.header h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.subtitle {
    font-size: 1.1rem;
    opacity: 0.9;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 40px;
}

.stat-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 15px;
    padding: 25px;
    text-align: center;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.2);
    transition: transform 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-5px);
}

.stat-number {
    font-size: 2.5rem;
    font-weight: bold;
    margin-bottom: 10px;
}

.stat-label {
    font-size: 1rem;
    color: #666;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.pending { color: #ff9800; }
.executing { color: #2196f3; }
.completed { color: #4caf50; }
.failed { color: #f44336; }
.total { color: #9c27b0; }

.control-panel {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 15px;
    padding: 30px;
    margin-bottom: 30px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.control-panel h2 {
    margin-bottom: 20px;
    color: #333;
}

.button-group {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    margin-bottom: 20px;
}

.btn {
    padding: 12px 24px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 600;
    transition: all 0.3s ease;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.btn-email {
    background: linear-gradient(135deg, #ff6b6b, #ee5a52);
    color: white;
}

.btn-report {
    background: linear-gradient(135deg, #4ecdc4, #44a08d);
    color: white;
}

.btn-data {
    background: linear-gradient(135deg, #45b7d1, #96c93d);
    color: white;
}

.btn-refresh {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
}

.btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.tasks-section {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 15px;
    padding: 30px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.tasks-section h2 {
    margin-bottom: 20px;
    color: #333;
}

.task-item {
    background: #f8f9fa;
    border-radius: 10px;
    padding: 20px;
    margin-bottom: 15px;
    border-left: 4px solid #ddd;
    transition: all 0.3s ease;
}

.task-item:hover {
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.task-item.executing {
    border-left-color: #2196f3;
    background: linear-gradient(90deg, rgba(33, 150, 243, 0.1), transparent);
}

.task-item.completed {
    border-left-color: #4caf50;
    background: linear-gradient(90deg, rgba(76, 175, 80, 0.1), transparent);
}

.task-item.failed {
    border-left-color: #f44336;
    background: linear-gradient(90deg, rgba(244, 67, 54, 0.1), transparent);
}

.task-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.task-name {
    font-weight: bold;
    font-size: 1.1rem;
}

.task-status {
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: bold;
    text-transform: uppercase;
    color: white;
}

.status-pending { background: #ff9800; }
.status-submitted { background: #607d8b; }
.status-executing { background: #2196f3; }
.status-completed { background: #4caf50; }
.status-failed { background: #f44336; }

.task-meta {
    font-size: 0.9rem;
    color: #666;
    margin-top: 10px;
}

.loading {
    text-align: center;
    padding: 40px;
    color: #666;
}

.spinner {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3498db;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
    .container {
        padding: 10px;
    }
    
    .header h1 {
        font-size: 2rem;
    }
    
    .button-group {
        flex-direction: column;
    }
    
    .btn {
        width: 100%;
    }
}
EOF

# Create JavaScript
cat > src/main/resources/static/js/dashboard.js << 'EOF'
class TaskDashboard {
    constructor() {
        this.tasks = [];
        this.isLoading = false;
        this.init();
    }

    init() {
        this.attachEventListeners();
        this.loadTasks();
        this.startAutoRefresh();
    }

    attachEventListeners() {
        document.getElementById('createEmailTask').addEventListener('click', () => this.createSampleTask('email'));
        document.getElementById('createReportTask').addEventListener('click', () => this.createSampleTask('report'));
        document.getElementById('createDataTask').addEventListener('click', () => this.createSampleTask('data'));
        document.getElementById('refreshTasks').addEventListener('click', () => this.loadTasks());
    }

    async createSampleTask(type) {
        try {
            const response = await fetch(`/api/tasks/sample/${type}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            
            if (response.ok) {
                const task = await response.json();
                console.log(`Created ${type} task:`, task);
                this.loadTasks(); // Refresh the task list
            }
        } catch (error) {
            console.error('Error creating task:', error);
        }
    }

    async loadTasks() {
        if (this.isLoading) return;
        
        this.isLoading = true;
        this.showLoading();

        try {
            const response = await fetch('/api/tasks');
            if (response.ok) {
                this.tasks = await response.json();
                this.renderTasks();
                this.updateStats();
            }
        } catch (error) {
            console.error('Error loading tasks:', error);
        } finally {
            this.isLoading = false;
        }
    }

    renderTasks() {
        const container = document.getElementById('tasksList');
        if (this.tasks.length === 0) {
            container.innerHTML = '<div class="loading">No tasks found. Create some tasks to get started!</div>';
            return;
        }

        const html = this.tasks.slice(0, 20).map(task => this.renderTask(task)).join('');
        container.innerHTML = html;
    }

    renderTask(task) {
        const statusClass = task.status.toLowerCase();
        const timeAgo = this.getTimeAgo(task.createdAt);
        
        return `
            <div class="task-item ${statusClass}">
                <div class="task-header">
                    <div class="task-name">${task.name}</div>
                    <div class="task-status status-${statusClass}">${task.status}</div>
                </div>
                <div class="task-meta">
                    <strong>Type:</strong> ${task.type} | 
                    <strong>Created:</strong> ${timeAgo}
                    ${task.result ? `<br><strong>Result:</strong> ${task.result}` : ''}
                    ${task.errorMessage ? `<br><strong>Error:</strong> ${task.errorMessage}` : ''}
                </div>
            </div>
        `;
    }

    updateStats() {
        const stats = this.calculateStats();
        
        document.getElementById('totalTasks').textContent = stats.total;
        document.getElementById('pendingTasks').textContent = stats.pending;
        document.getElementById('executingTasks').textContent = stats.executing;
        document.getElementById('completedTasks').textContent = stats.completed;
        document.getElementById('failedTasks').textContent = stats.failed;
    }

    calculateStats() {
        return {
            total: this.tasks.length,
            pending: this.tasks.filter(t => t.status === 'PENDING' || t.status === 'SUBMITTED').length,
            executing: this.tasks.filter(t => t.status === 'EXECUTING').length,
            completed: this.tasks.filter(t => t.status === 'COMPLETED').length,
            failed: this.tasks.filter(t => t.status === 'FAILED').length
        };
    }

    showLoading() {
        document.getElementById('tasksList').innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                Loading tasks...
            </div>
        `;
    }

    getTimeAgo(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffSecs = Math.floor(diffMs / 1000);
        const diffMins = Math.floor(diffSecs / 60);
        const diffHours = Math.floor(diffMins / 60);

        if (diffSecs < 60) return `${diffSecs}s ago`;
        if (diffMins < 60) return `${diffMins}m ago`;
        if (diffHours < 24) return `${diffHours}h ago`;
        return date.toLocaleDateString();
    }

    startAutoRefresh() {
        setInterval(() => {
            if (!this.isLoading) {
                this.loadTasks();
            }
        }, 5000); // Refresh every 5 seconds
    }
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', () => {
    new TaskDashboard();
});
EOF

# Create HTML Template
cat > src/main/resources/templates/dashboard.html << 'EOF'
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Async Task Scheduler Dashboard</title>
    <link rel="stylesheet" href="/css/dashboard.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔄 Async Task Scheduler</h1>
            <p class="subtitle">Real-time monitoring of asynchronous task execution</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number total" id="totalTasks" th:text="${totalTasks}">0</div>
                <div class="stat-label">Total Tasks</div>
            </div>
            <div class="stat-card">
                <div class="stat-number pending" id="pendingTasks" th:text="${pendingTasks}">0</div>
                <div class="stat-label">Pending/Submitted</div>
            </div>
            <div class="stat-card">
                <div class="stat-number executing" id="executingTasks" th:text="${executingTasks}">0</div>
                <div class="stat-label">Executing</div>
            </div>
            <div class="stat-card">
                <div class="stat-number completed" id="completedTasks" th:text="${completedTasks}">0</div>
                <div class="stat-label">Completed</div>
            </div>
            <div class="stat-card">
                <div class="stat-number failed" id="failedTasks" th:text="${failedTasks}">0</div>
                <div class="stat-label">Failed</div>
            </div>
        </div>

        <div class="control-panel">
            <h2>📋 Task Controls</h2>
            <div class="button-group">
                <button id="createEmailTask" class="btn btn-email">Create Email Task</button>
                <button id="createReportTask" class="btn btn-report">Create Report Task</button>
                <button id="createDataTask" class="btn btn-data">Create Data Task</button>
                <button id="refreshTasks" class="btn btn-refresh">Refresh</button>
            </div>
        </div>

        <div class="tasks-section">
            <h2>📊 Recent Tasks</h2>
            <div id="tasksList">
                <div class="loading">Loading tasks...</div>
            </div>
        </div>
    </div>

    <script src="/js/dashboard.js"></script>
</body>
</html>
EOF

# Create test files
cat > src/test/java/com/scheduler/AsyncTaskSchedulerApplicationTests.java << 'EOF'
package com.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AsyncTaskSchedulerApplicationTests {

    @Test
    void contextLoads() {
    }
}
EOF

# Create build script
cat > build.sh << 'EOF'
#!/bin/bash
set -e

echo "🔨 Building Async Task Scheduler..."

# Check if Java 21 is available
if ! java -version 2>&1 | grep -q "21"; then
    echo "⚠️  Warning: Java 21 not found. This project requires Java 21 for optimal performance."
    echo "Install Java 21 or set JAVA_HOME to continue."
fi

# Make gradlew executable
chmod +x gradlew

# Clean and build
echo "📦 Running Gradle build..."
./gradlew clean build -x test

# Run tests
echo "🧪 Running tests..."
./gradlew test

echo "✅ Build completed successfully!"
echo ""
echo "Next steps:"
echo "1. Run './start.sh' to start the application"
echo "2. Open http://localhost:8080 to view the dashboard"
echo "3. Create sample tasks and watch async execution in real-time"
EOF

# Create start script
cat > start.sh << 'EOF'
#!/bin/bash
set -e

echo "🚀 Starting Async Task Scheduler..."

# Build if needed
if [ ! -f "build/libs/async-task-scheduler-1.0.0.jar" ]; then
    echo "📦 Building application first..."
    ./build.sh
fi

# Start the application
echo "🔥 Starting Spring Boot application..."
echo "Dashboard will be available at: http://localhost:8080"
echo "API documentation at: http://localhost:8080/swagger-ui.html"
echo "H2 Console at: http://localhost:8080/h2-console"
echo ""
echo "Press Ctrl+C to stop the application"

java -jar build/libs/async-task-scheduler-1.0.0.jar
EOF

# Create stop script
cat > stop.sh << 'EOF'
#!/bin/bash

echo "🛑 Stopping Async Task Scheduler..."

# Find and kill the Spring Boot process
PID=$(ps aux | grep '[a]sync-task-scheduler-1.0.0.jar' | awk '{print $2}')

if [ -n "$PID" ]; then
    echo "Stopping process $PID..."
    kill $PID
    echo "✅ Application stopped"
else
    echo "No running application found"
fi
EOF

# Create demo script
cat > demo.sh << 'EOF'
#!/bin/bash
set -e

echo "🎬 Running Async Task Scheduler Demo..."

BASE_URL="http://localhost:8080"

# Wait for application to start
echo "Waiting for application to start..."
sleep 10

# Check if app is running
if ! curl -s "$BASE_URL/actuator/health" > /dev/null; then
    echo "❌ Application not running. Start with './start.sh' first"
    exit 1
fi

echo "✅ Application is running!"
echo ""

# Create sample tasks
echo "📧 Creating email tasks..."
curl -s -X POST "$BASE_URL/api/tasks/sample/email" > /dev/null
curl -s -X POST "$BASE_URL/api/tasks/sample/email" > /dev/null

echo "📊 Creating report tasks..."
curl -s -X POST "$BASE_URL/api/tasks/sample/report" > /dev/null
curl -s -X POST "$BASE_URL/api/tasks/sample/report" > /dev/null

echo "💾 Creating data tasks..."
curl -s -X POST "$BASE_URL/api/tasks/sample/data" > /dev/null
curl -s -X POST "$BASE_URL/api/tasks/sample/data" > /dev/null

echo ""
echo "🎉 Demo tasks created!"
echo "Open http://localhost:8080 to see async execution in real-time"
echo "Watch as multiple tasks execute concurrently in different thread pools"
EOF

# Make scripts executable
chmod +x build.sh start.sh stop.sh demo.sh

echo "✅ Project created successfully!"
echo ""
echo "📂 Project structure:"
echo "   $PROJECT_DIR/"
echo "   ├── src/main/java/com/scheduler/"
echo "   │   ├── config/          # Async thread pool configuration"
echo "   │   ├── controller/      # REST API and web controllers"
echo "   │   ├── model/           # Task entities and enums"
echo "   │   ├── repository/      # Data access layer"
echo "   │   └── service/         # Async business logic"
echo "   ├── src/main/resources/"
echo "   │   ├── static/          # CSS and JavaScript"
echo "   │   └── templates/       # Thymeleaf HTML templates"
echo "   ├── build.sh            # Build and test script"
echo "   ├── start.sh            # Start application"
echo "   ├── stop.sh             # Stop application"
echo "   └── demo.sh             # Demo script"
echo ""
echo "🚀 To get started:"
echo "   cd $PROJECT_NAME"
echo "   ./build.sh              # Build the project"
echo "   ./start.sh              # Start the application"
echo "   # In another terminal:"
echo "   ./demo.sh               # Create demo tasks"
echo ""
echo "🌐 Access points:"
echo "   Dashboard: http://localhost:8080"
echo "   API Docs:  http://localhost:8080/swagger-ui.html"
echo "   H2 Console: http://localhost:8080/h2-console"
echo ""
echo "🎯 Key features demonstrated:"
echo "   ✓ Async task execution with @Async"
echo "   ✓ Custom thread pools for different task types"
echo "   ✓ Real-time task status tracking"
echo "   ✓ Modern responsive dashboard"
echo "   ✓ RESTful API with OpenAPI documentation"
echo "   ✓ Concurrent processing demonstration"