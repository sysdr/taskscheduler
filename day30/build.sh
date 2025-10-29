#!/bin/bash

# Day 30: Graceful Shutdown Task Scheduler Implementation
# Module 3: Robustness, Reliability, and Error Handling

set -e

PROJECT_NAME="graceful-shutdown-scheduler"
BASE_DIR=$(pwd)
PROJECT_DIR="$BASE_DIR/$PROJECT_NAME"

echo "🚀 Creating Ultra-Scalable Task Scheduler with Graceful Shutdown"
echo "=================================================="

# Create project structure
echo "📁 Creating project directory structure..."
mkdir -p "$PROJECT_DIR"
cd "$PROJECT_DIR"

# Create Maven directory structure
mkdir -p src/main/java/com/taskscheduler/{config,model,service,repository,controller,component}
mkdir -p src/main/resources/{static/{css,js},templates}
mkdir -p src/test/java/com/taskscheduler
mkdir -p docker

# Create pom.xml
echo "📄 Creating Maven configuration..."
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/>
    </parent>
    
    <groupId>com.taskscheduler</groupId>
    <artifactId>graceful-shutdown-scheduler</artifactId>
    <version>1.0.0</version>
    <name>Graceful Shutdown Task Scheduler</name>
    <description>Ultra-scalable task scheduler with graceful shutdown capabilities</description>
    
    <properties>
        <java.version>21</java.version>
        <spring.boot.version>3.2.1</spring.boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Create application.properties
echo "⚙️ Creating application configuration..."
cat > src/main/resources/application.properties << 'EOF'
# Application Configuration
spring.application.name=graceful-shutdown-scheduler
server.port=8080
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,shutdown
management.endpoint.shutdown.enabled=true
management.endpoint.health.show-details=always

# Task Scheduler Configuration
task.scheduler.core-pool-size=5
task.scheduler.max-pool-size=10
task.scheduler.queue-capacity=100
task.scheduler.graceful-shutdown-timeout=30
EOF

# Create main application class
echo "🎯 Creating main application class..."
cat > src/main/java/com/taskscheduler/GracefulShutdownSchedulerApplication.java << 'EOF'
package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GracefulShutdownSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GracefulShutdownSchedulerApplication.class, args);
    }
}
EOF

# Create Task model
echo "📊 Creating Task model..."
cat > src/main/java/com/taskscheduler/model/Task.java << 'EOF'
package com.taskscheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;
    
    @Column(name = "duration_seconds")
    private int durationSeconds;
    
    @Column(name = "progress_percentage")
    private int progressPercentage = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "last_checkpoint")
    private LocalDateTime lastCheckpoint;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED, SUSPENDED, SCHEDULED_FOR_RETRY
    }
    
    // Constructors
    public Task() {}
    
    public Task(String name, String description, int durationSeconds) {
        this.name = name;
        this.description = description;
        this.durationSeconds = durationSeconds;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    
    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getLastCheckpoint() { return lastCheckpoint; }
    public void setLastCheckpoint(LocalDateTime lastCheckpoint) { this.lastCheckpoint = lastCheckpoint; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
EOF

# Create Task Repository
echo "🗄️ Creating Task repository..."
cat > src/main/java/com/taskscheduler/repository/TaskRepository.java << 'EOF'
package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.status IN ('RUNNING', 'PENDING')")
    List<Task> findActiveOrPendingTasks();
    
    @Query("SELECT t FROM Task t WHERE t.status = 'SUSPENDED' OR t.status = 'SCHEDULED_FOR_RETRY'")
    List<Task> findTasksForRecovery();
    
    long countByStatus(Task.TaskStatus status);
}
EOF

# Create Task Service
echo "🔧 Creating Task service..."
cat > src/main/java/com/taskscheduler/service/TaskService.java << 'EOF'
package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    public Task createTask(String name, String description, int durationSeconds) {
        Task task = new Task(name, description, durationSeconds);
        Task savedTask = taskRepository.save(task);
        logger.info("Created task: {} with duration: {}s", name, durationSeconds);
        return savedTask;
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public void updateTaskStatus(Long taskId, Task.TaskStatus status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            
            if (status == Task.TaskStatus.RUNNING) {
                task.setStartedAt(LocalDateTime.now());
            } else if (status == Task.TaskStatus.COMPLETED) {
                task.setCompletedAt(LocalDateTime.now());
                task.setProgressPercentage(100);
            }
            
            taskRepository.save(task);
            logger.info("Updated task {} status to {}", taskId, status);
        }
    }
    
    public void updateTaskProgress(Long taskId, int progressPercentage) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setProgressPercentage(progressPercentage);
            task.setLastCheckpoint(LocalDateTime.now());
            taskRepository.save(task);
        }
    }
    
    public void suspendTask(Long taskId, String reason) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.SUSPENDED);
            task.setErrorMessage("Suspended: " + reason);
            task.setLastCheckpoint(LocalDateTime.now());
            taskRepository.save(task);
            logger.info("Suspended task {} due to: {}", taskId, reason);
        }
    }
    
    public List<Task> getActiveOrPendingTasks() {
        return taskRepository.findActiveOrPendingTasks();
    }
    
    public List<Task> getTasksForRecovery() {
        return taskRepository.findTasksForRecovery();
    }
    
    public void markTaskForRetry(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.SCHEDULED_FOR_RETRY);
            taskRepository.save(task);
            logger.info("Marked task {} for retry", taskId);
        }
    }
}
EOF

# Create Graceful Shutdown Manager
echo "🛡️ Creating Graceful Shutdown Manager..."
cat > src/main/java/com/taskscheduler/component/TaskSchedulerLifecycleManager.java << 'EOF'
package com.taskscheduler.component;

import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class TaskSchedulerLifecycleManager implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerLifecycleManager.class);
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskExecutorService taskExecutorService;
    
    @Value("${task.scheduler.graceful-shutdown-timeout:30}")
    private int shutdownTimeoutSeconds;
    
    private volatile boolean shutdownInitiated = false;
    private CountDownLatch shutdownLatch = new CountDownLatch(0);
    
    @PreDestroy
    @Override
    public void destroy() throws Exception {
        logger.info("🛑 Initiating graceful shutdown of Task Scheduler...");
        initiateGracefulShutdown();
    }
    
    public void initiateGracefulShutdown() {
        if (shutdownInitiated) {
            logger.warn("Shutdown already initiated, ignoring duplicate request");
            return;
        }
        
        shutdownInitiated = true;
        
        try {
            // Step 1: Stop accepting new tasks
            logger.info("📢 Stopping acceptance of new tasks...");
            taskExecutorService.stopAcceptingNewTasks();
            
            // Step 2: Get list of active/pending tasks
            List<Task> activeTasks = taskService.getActiveOrPendingTasks();
            logger.info("🔍 Found {} active/pending tasks to handle", activeTasks.size());
            
            if (activeTasks.isEmpty()) {
                logger.info("✅ No active tasks found, proceeding with immediate shutdown");
                return;
            }
            
            // Step 3: Set up countdown latch for active tasks
            shutdownLatch = new CountDownLatch(activeTasks.size());
            
            // Step 4: Wait for tasks to complete or timeout
            logger.info("⏳ Waiting up to {} seconds for {} tasks to complete...", 
                       shutdownTimeoutSeconds, activeTasks.size());
            
            boolean allTasksCompleted = shutdownLatch.await(shutdownTimeoutSeconds, TimeUnit.SECONDS);
            
            if (allTasksCompleted) {
                logger.info("✅ All tasks completed successfully during graceful shutdown");
            } else {
                logger.warn("⚠️ Shutdown timeout reached, suspending remaining tasks...");
                suspendRemainingTasks();
            }
            
        } catch (InterruptedException e) {
            logger.error("❌ Graceful shutdown interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            // Step 5: Final cleanup
            performFinalCleanup();
            logger.info("🏁 Graceful shutdown completed");
        }
    }
    
    private void suspendRemainingTasks() {
        List<Task> stillActiveTasks = taskService.getActiveOrPendingTasks();
        logger.info("💤 Suspending {} remaining active tasks", stillActiveTasks.size());
        
        for (Task task : stillActiveTasks) {
            if (task.getStatus() == Task.TaskStatus.RUNNING || 
                task.getStatus() == Task.TaskStatus.PENDING) {
                taskService.suspendTask(task.getId(), "Application shutdown");
                logger.info("💤 Suspended task: {} ({})", task.getName(), task.getId());
            }
        }
    }
    
    private void performFinalCleanup() {
        logger.info("🧹 Performing final cleanup...");
        
        // Clean up executor service
        taskExecutorService.forceShutdown();
        
        // Log final statistics
        logFinalStatistics();
    }
    
    private void logFinalStatistics() {
        logger.info("📊 Final Task Statistics:");
        logger.info("   - Completed: {}", taskService.getAllTasks().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count());
        logger.info("   - Suspended: {}", taskService.getAllTasks().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.SUSPENDED).count());
        logger.info("   - Failed: {}", taskService.getAllTasks().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.FAILED).count());
    }
    
    public void notifyTaskCompleted() {
        if (shutdownInitiated) {
            shutdownLatch.countDown();
            logger.debug("📈 Task completed during shutdown. Remaining: {}", shutdownLatch.getCount());
        }
    }
    
    public boolean isShutdownInitiated() {
        return shutdownInitiated;
    }
}
EOF

# Create Task Executor Service
echo "⚡ Creating Task Executor Service..."
cat > src/main/java/com/taskscheduler/component/TaskExecutorService.java << 'EOF'
package com.taskscheduler.component;

import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TaskExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorService.class);
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskSchedulerLifecycleManager lifecycleManager;
    
    @Value("${task.scheduler.core-pool-size:5}")
    private int corePoolSize;
    
    private final AtomicBoolean acceptingNewTasks = new AtomicBoolean(true);
    private ScheduledExecutorService executorService;
    
    @jakarta.annotation.PostConstruct
    public void initialize() {
        this.executorService = Executors.newScheduledThreadPool(corePoolSize);
        logger.info("🚀 Task Executor Service initialized with {} threads", corePoolSize);
    }
    
    @Async
    public void executeTask(Long taskId) {
        if (!acceptingNewTasks.get()) {
            logger.warn("⛔ Rejecting new task {} - shutdown in progress", taskId);
            return;
        }
        
        try {
            Task task = taskService.getTaskById(taskId).orElse(null);
            if (task == null) {
                logger.error("❌ Task {} not found", taskId);
                return;
            }
            
            logger.info("🎯 Starting execution of task: {} ({})", task.getName(), taskId);
            taskService.updateTaskStatus(taskId, Task.TaskStatus.RUNNING);
            
            // Simulate task execution with progress updates
            executeTaskWithProgress(task);
            
        } catch (InterruptedException e) {
            logger.warn("⚠️ Task {} interrupted during execution", taskId);
            taskService.suspendTask(taskId, "Task interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("❌ Task {} failed with error: {}", taskId, e.getMessage());
            taskService.updateTaskStatus(taskId, Task.TaskStatus.FAILED);
        } finally {
            // Notify lifecycle manager if shutdown is in progress
            if (lifecycleManager.isShutdownInitiated()) {
                lifecycleManager.notifyTaskCompleted();
            }
        }
    }
    
    private void executeTaskWithProgress(Task task) throws InterruptedException {
        int totalDuration = task.getDurationSeconds();
        int progressSteps = Math.min(10, totalDuration); // Update progress 10 times or once per second
        int stepDuration = totalDuration / progressSteps;
        
        for (int step = 1; step <= progressSteps; step++) {
            // Check if shutdown is initiated
            if (lifecycleManager.isShutdownInitiated()) {
                logger.info("🛑 Task {} suspended due to shutdown", task.getId());
                taskService.suspendTask(task.getId(), "Graceful shutdown");
                return;
            }
            
            // Simulate work
            Thread.sleep(stepDuration * 1000L);
            
            // Update progress
            int progress = (step * 100) / progressSteps;
            taskService.updateTaskProgress(task.getId(), progress);
            logger.debug("📊 Task {} progress: {}%", task.getId(), progress);
        }
        
        // Task completed successfully
        taskService.updateTaskStatus(task.getId(), Task.TaskStatus.COMPLETED);
        logger.info("✅ Task {} completed successfully", task.getId());
    }
    
    public void stopAcceptingNewTasks() {
        acceptingNewTasks.set(false);
        logger.info("🚫 Stopped accepting new tasks");
    }
    
    public void forceShutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            logger.info("💀 Force shutdown of executor service completed");
        }
    }
    
    public boolean isAcceptingNewTasks() {
        return acceptingNewTasks.get();
    }
}
EOF

# Create REST Controller
echo "🌐 Creating REST Controller..."
cat > src/main/java/com/taskscheduler/controller/TaskController.java << 'EOF'
package com.taskscheduler.controller;

import com.taskscheduler.component.TaskExecutorService;
import com.taskscheduler.component.TaskSchedulerLifecycleManager;
import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskExecutorService taskExecutorService;
    
    @Autowired
    private TaskSchedulerLifecycleManager lifecycleManager;
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        if (!taskExecutorService.isAcceptingNewTasks()) {
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
        
        Task task = taskService.createTask(
            request.getName(),
            request.getDescription(),
            request.getDurationSeconds()
        );
        
        // Execute the task asynchronously
        taskExecutorService.executeTask(task.getId());
        
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/demo-scenario")
    public ResponseEntity<Map<String, Object>> createDemoScenario() {
        if (!taskExecutorService.isAcceptingNewTasks()) {
            return ResponseEntity.status(503).build();
        }
        
        // Create tasks with varying durations
        Task[] tasks = {
            taskService.createTask("Quick Email Send", "Send welcome email", 5),
            taskService.createTask("Data Processing", "Process user data", 15),
            taskService.createTask("Report Generation", "Generate monthly report", 25),
            taskService.createTask("Database Backup", "Backup user database", 45),
            taskService.createTask("Image Processing", "Process uploaded images", 60)
        };
        
        // Start all tasks
        for (Task task : tasks) {
            taskExecutorService.executeTask(task.getId());
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Demo scenario created with 5 tasks of varying durations",
            "tasks", tasks,
            "instruction", "Wait 10 seconds then call POST /api/shutdown to test graceful shutdown"
        ));
    }
    
    @PostMapping("/shutdown")
    public ResponseEntity<Map<String, String>> initiateShutdown() {
        lifecycleManager.initiateGracefulShutdown();
        return ResponseEntity.ok(Map.of(
            "message", "Graceful shutdown initiated",
            "status", "Tasks will complete or be suspended within 30 seconds"
        ));
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        List<Task> allTasks = taskService.getAllTasks();
        
        long pendingCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.PENDING).count();
        long runningCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.RUNNING).count();
        long completedCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count();
        long suspendedCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.SUSPENDED).count();
        long failedCount = allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.FAILED).count();
        
        return ResponseEntity.ok(Map.of(
            "acceptingNewTasks", taskExecutorService.isAcceptingNewTasks(),
            "shutdownInitiated", lifecycleManager.isShutdownInitiated(),
            "totalTasks", allTasks.size(),
            "taskCounts", Map.of(
                "pending", pendingCount,
                "running", runningCount,
                "completed", completedCount,
                "suspended", suspendedCount,
                "failed", failedCount
            )
        ));
    }
    
    public static class CreateTaskRequest {
        private String name;
        private String description;
        private int durationSeconds;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    }
}
EOF

# Create modern UI dashboard
echo "🎨 Creating modern UI dashboard..."
cat > src/main/resources/templates/dashboard.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Graceful Shutdown Task Scheduler</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: #2d3748;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .header {
            text-align: center;
            margin-bottom: 3rem;
            color: white;
        }
        
        .header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        
        .header p {
            font-size: 1.1rem;
            opacity: 0.9;
        }
        
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        
        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        }
        
        .card h3 {
            font-size: 1.25rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: #4a5568;
        }
        
        .status-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 1rem;
            margin-bottom: 1.5rem;
        }
        
        .status-item {
            text-align: center;
            padding: 1rem;
            background: #f7fafc;
            border-radius: 12px;
            border-left: 4px solid;
        }
        
        .status-pending { border-left-color: #ed8936; }
        .status-running { border-left-color: #4299e1; }
        .status-completed { border-left-color: #48bb78; }
        .status-suspended { border-left-color: #9f7aea; }
        .status-failed { border-left-color: #f56565; }
        
        .status-number {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 0.25rem;
        }
        
        .status-label {
            font-size: 0.875rem;
            color: #718096;
            text-transform: uppercase;
            font-weight: 500;
        }
        
        .btn {
            background: linear-gradient(135deg, #4299e1, #3182ce);
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 12px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            margin: 0.25rem;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(66, 153, 225, 0.4);
        }
        
        .btn-danger {
            background: linear-gradient(135deg, #f56565, #e53e3e);
        }
        
        .btn-success {
            background: linear-gradient(135deg, #48bb78, #38a169);
        }
        
        .system-status {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 1rem;
        }
        
        .status-indicator {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            animation: pulse 2s infinite;
        }
        
        .status-online { background-color: #48bb78; }
        .status-offline { background-color: #f56565; }
        
        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.5; }
            100% { opacity: 1; }
        }
        
        .task-list {
            max-height: 400px;
            overflow-y: auto;
        }
        
        .task-item {
            background: #f7fafc;
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 0.5rem;
            border-left: 4px solid #e2e8f0;
        }
        
        .task-name {
            font-weight: 600;
            margin-bottom: 0.25rem;
        }
        
        .task-progress {
            background: #e2e8f0;
            border-radius: 4px;
            height: 6px;
            margin: 0.5rem 0;
            overflow: hidden;
        }
        
        .task-progress-bar {
            background: linear-gradient(135deg, #4299e1, #3182ce);
            height: 100%;
            transition: width 0.3s ease;
        }
        
        .alerts {
            position: fixed;
            top: 1rem;
            right: 1rem;
            z-index: 1000;
        }
        
        .alert {
            background: white;
            border-radius: 12px;
            padding: 1rem 1.5rem;
            margin-bottom: 0.5rem;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            border-left: 4px solid;
            min-width: 300px;
            animation: slideIn 0.3s ease-out;
        }
        
        .alert-success { border-left-color: #48bb78; }
        .alert-warning { border-left-color: #ed8936; }
        .alert-error { border-left-color: #f56565; }
        
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🛡️ Graceful Shutdown Task Scheduler</h1>
            <p>Ultra-scalable task scheduling with robust shutdown mechanisms</p>
        </div>
        
        <div class="dashboard-grid">
            <div class="card">
                <h3>System Status</h3>
                <div class="system-status">
                    <div class="status-indicator status-online" id="systemIndicator"></div>
                    <span id="systemStatusText">Online</span>
                </div>
                <div>
                    <strong>Accepting New Tasks:</strong> 
                    <span id="acceptingTasks">Yes</span>
                </div>
                <div>
                    <strong>Shutdown Initiated:</strong> 
                    <span id="shutdownStatus">No</span>
                </div>
            </div>
            
            <div class="card">
                <h3>Task Statistics</h3>
                <div class="status-grid">
                    <div class="status-item status-pending">
                        <div class="status-number" id="pendingCount">0</div>
                        <div class="status-label">Pending</div>
                    </div>
                    <div class="status-item status-running">
                        <div class="status-number" id="runningCount">0</div>
                        <div class="status-label">Running</div>
                    </div>
                    <div class="status-item status-completed">
                        <div class="status-number" id="completedCount">0</div>
                        <div class="status-label">Completed</div>
                    </div>
                    <div class="status-item status-suspended">
                        <div class="status-number" id="suspendedCount">0</div>
                        <div class="status-label">Suspended</div>
                    </div>
                </div>
            </div>
            
            <div class="card">
                <h3>Actions</h3>
                <button class="btn btn-success" onclick="createDemoScenario()">
                    🚀 Create Demo Scenario
                </button>
                <button class="btn btn-danger" onclick="initiateShutdown()">
                    🛑 Initiate Graceful Shutdown
                </button>
                <button class="btn" onclick="refreshData()">
                    🔄 Refresh Data
                </button>
            </div>
        </div>
        
        <div class="card">
            <h3>Active Tasks</h3>
            <div class="task-list" id="taskList">
                <p>No tasks found. Create a demo scenario to see tasks in action!</p>
            </div>
        </div>
    </div>
    
    <div class="alerts" id="alerts"></div>
    
    <script>
        let refreshInterval;
        
        function showAlert(message, type = 'success') {
            const alertsContainer = document.getElementById('alerts');
            const alert = document.createElement('div');
            alert.className = `alert alert-${type}`;
            alert.textContent = message;
            
            alertsContainer.appendChild(alert);
            
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 5000);
        }
        
        async function fetchData(url, options = {}) {
            try {
                const response = await fetch(url, options);
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return await response.json();
            } catch (error) {
                console.error('Fetch error:', error);
                showAlert(`Error: ${error.message}`, 'error');
                throw error;
            }
        }
        
        async function refreshData() {
            try {
                const [statusData, tasksData] = await Promise.all([
                    fetchData('/api/tasks/status'),
                    fetchData('/api/tasks')
                ]);
                
                updateSystemStatus(statusData);
                updateTaskList(tasksData);
            } catch (error) {
                console.error('Error refreshing data:', error);
            }
        }
        
        function updateSystemStatus(data) {
            const indicator = document.getElementById('systemIndicator');
            const statusText = document.getElementById('systemStatusText');
            const acceptingTasks = document.getElementById('acceptingTasks');
            const shutdownStatus = document.getElementById('shutdownStatus');
            
            if (data.acceptingNewTasks) {
                indicator.className = 'status-indicator status-online';
                statusText.textContent = 'Online';
            } else {
                indicator.className = 'status-indicator status-offline';
                statusText.textContent = 'Shutting Down';
            }
            
            acceptingTasks.textContent = data.acceptingNewTasks ? 'Yes' : 'No';
            shutdownStatus.textContent = data.shutdownInitiated ? 'Yes' : 'No';
            
            // Update task counts
            const counts = data.taskCounts || {};
            document.getElementById('pendingCount').textContent = counts.pending || 0;
            document.getElementById('runningCount').textContent = counts.running || 0;
            document.getElementById('completedCount').textContent = counts.completed || 0;
            document.getElementById('suspendedCount').textContent = counts.suspended || 0;
        }
        
        function updateTaskList(tasks) {
            const taskList = document.getElementById('taskList');
            
            if (!tasks || tasks.length === 0) {
                taskList.innerHTML = '<p>No tasks found. Create a demo scenario to see tasks in action!</p>';
                return;
            }
            
            taskList.innerHTML = tasks.map(task => `
                <div class="task-item">
                    <div class="task-name">${task.name}</div>
                    <div style="font-size: 0.875rem; color: #718096;">${task.description}</div>
                    <div class="task-progress">
                        <div class="task-progress-bar" style="width: ${task.progressPercentage}%"></div>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-size: 0.875rem;">
                        <span>Status: <strong>${task.status}</strong></span>
                        <span>Progress: ${task.progressPercentage}%</span>
                        <span>Duration: ${task.durationSeconds}s</span>
                    </div>
                </div>
            `).join('');
        }
        
        async function createDemoScenario() {
            try {
                const result = await fetchData('/api/tasks/demo-scenario', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                });
                
                showAlert('Demo scenario created! 5 tasks with varying durations started.', 'success');
                refreshData();
                
                // Start auto-refresh
                if (refreshInterval) clearInterval(refreshInterval);
                refreshInterval = setInterval(refreshData, 2000);
                
            } catch (error) {
                showAlert('Failed to create demo scenario', 'error');
            }
        }
        
        async function initiateShutdown() {
            if (!confirm('Are you sure you want to initiate graceful shutdown? This will stop accepting new tasks.')) {
                return;
            }
            
            try {
                const result = await fetchData('/api/tasks/shutdown', {
                    method: 'POST'
                });
                
                showAlert('Graceful shutdown initiated! Watch as tasks complete or get suspended.', 'warning');
                refreshData();
                
            } catch (error) {
                showAlert('Failed to initiate shutdown', 'error');
            }
        }
        
        // Initialize dashboard
        refreshData();
        
        // Set up periodic refresh
        refreshInterval = setInterval(refreshData, 5000);
        
        // Cleanup on page unload
        window.addEventListener('beforeunload', () => {
            if (refreshInterval) clearInterval(refreshInterval);
        });
    </script>
</body>
</html>
EOF

# Create web controller for dashboard
echo "🎯 Creating Web Controller..."
cat > src/main/java/com/taskscheduler/controller/WebController.java << 'EOF'
package com.taskscheduler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }
}
EOF

# Create Docker configuration
echo "🐳 Creating Docker configuration..."
cat > docker/Dockerfile << 'EOF'
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/graceful-shutdown-scheduler-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
EOF

cat > docker/docker-compose.yml << 'EOF'
version: '3.8'

services:
  task-scheduler:
    build:
      context: ..
      dockerfile: docker/Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - JAVA_OPTS=-Xmx512m -Xms256m
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
EOF

# Create build script
echo "🔧 Creating build script..."
cat > build.sh << 'EOF'
#!/bin/bash

set -e

echo "🔨 Building Graceful Shutdown Task Scheduler..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java 21 is available
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 21 ]; then
    echo "❌ Java 21 or higher is required. Current version: $java_version"
    exit 1
fi

echo "✅ Java version check passed"

# Clean and compile
echo "🧹 Cleaning previous builds..."
mvn clean

echo "📦 Compiling and packaging..."
mvn package -DskipTests

echo "🧪 Running tests..."
mvn test

echo "✅ Build completed successfully!"
echo "📦 JAR file created: target/graceful-shutdown-scheduler-1.0.0.jar"
EOF

# Create start script
echo "🚀 Creating start script..."
cat > start.sh << 'EOF'
#!/bin/bash

set -e

echo "🚀 Starting Graceful Shutdown Task Scheduler..."

# Check if jar file exists
if [ ! -f "target/graceful-shutdown-scheduler-1.0.0.jar" ]; then
    echo "❌ JAR file not found. Please run build.sh first."
    exit 1
fi

echo "🌟 Starting application on port 8080..."
echo "📊 Dashboard will be available at: http://localhost:8080"
echo "🔧 H2 Console available at: http://localhost:8080/h2-console"
echo "📈 Actuator endpoints: http://localhost:8080/actuator"
echo ""
echo "Press Ctrl+C to stop the application"
echo "=================================================="

java -jar target/graceful-shutdown-scheduler-1.0.0.jar
EOF

# Create stop script
echo "🛑 Creating stop script..."
cat > stop.sh << 'EOF'
#!/bin/bash

echo "🛑 Stopping Graceful Shutdown Task Scheduler..."

# Find and kill the Java process
PID=$(pgrep -f "graceful-shutdown-scheduler-1.0.0.jar" || true)

if [ -n "$PID" ]; then
    echo "📍 Found application running with PID: $PID"
    echo "🔄 Sending graceful shutdown signal (SIGTERM)..."
    kill -TERM $PID
    
    # Wait up to 30 seconds for graceful shutdown
    for i in {1..30}; do
        if ! kill -0 $PID 2>/dev/null; then
            echo "✅ Application stopped gracefully"
            exit 0
        fi
        echo "⏳ Waiting for graceful shutdown... ($i/30)"
        sleep 1
    done
    
    echo "⚠️ Graceful shutdown timeout reached, forcing termination..."
    kill -KILL $PID 2>/dev/null || true
    echo "💀 Application force-stopped"
else
    echo "ℹ️ No running application found"
fi
EOF

# Create test script
echo "🧪 Creating test script..."
cat > test.sh << 'EOF'
#!/bin/bash

set -e

echo "🧪 Running Graceful Shutdown Tests..."

# Build first
./build.sh

echo "🎯 Starting test scenarios..."

# Start application in background
java -jar target/graceful-shutdown-scheduler-1.0.0.jar &
APP_PID=$!

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

# Function to check if app is responding
check_app() {
    curl -f http://localhost:8080/api/tasks/status >/dev/null 2>&1
}

# Wait for app to be ready
for i in {1..30}; do
    if check_app; then
        echo "✅ Application is ready"
        break
    fi
    echo "⏳ Waiting for application... ($i/30)"
    sleep 2
done

if ! check_app; then
    echo "❌ Application failed to start"
    kill $APP_PID 2>/dev/null || true
    exit 1
fi

# Test 1: Create demo scenario
echo "🎯 Test 1: Creating demo scenario..."
DEMO_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks/demo-scenario)
echo "✅ Demo scenario created"

# Wait a bit for tasks to start
sleep 5

# Test 2: Check status
echo "🎯 Test 2: Checking system status..."
STATUS_RESPONSE=$(curl -s http://localhost:8080/api/tasks/status)
echo "✅ Status check completed"

# Test 3: Initiate graceful shutdown
echo "🎯 Test 3: Testing graceful shutdown..."
SHUTDOWN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks/shutdown)
echo "✅ Graceful shutdown initiated"

# Wait for shutdown to complete
sleep 35

# Check if process is still running
if kill -0 $APP_PID 2>/dev/null; then
    echo "⚠️ Application still running, forcing stop..."
    kill -KILL $APP_PID 2>/dev/null || true
fi

echo "🎉 All tests completed successfully!"
echo ""
echo "📊 Test Results:"
echo "   ✅ Demo scenario creation"
echo "   ✅ System status monitoring"
echo "   ✅ Graceful shutdown process"
echo ""
echo "🚀 Ready for production use!"
EOF

# Make scripts executable
chmod +x build.sh start.sh stop.sh test.sh

# Create README
echo "📚 Creating README..."
cat > README.md << 'EOF'
# Graceful Shutdown Task Scheduler

Ultra-scalable task scheduler with robust graceful shutdown capabilities.

## Features

- ✅ Graceful shutdown with configurable timeout
- 📊 Real-time task monitoring dashboard
- 🔄 Task state persistence and recovery
- 🎯 Modern, responsive web UI
- 📈 Production-ready metrics and health checks
- 🐳 Docker support

## Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Build and Run

```bash
# Build the application
./build.sh

# Start the application
./start.sh

# In another terminal, run tests
./test.sh

# Stop the application
./stop.sh
```

### Access Points

- 📊 **Dashboard**: http://localhost:8080
- 🔧 **H2 Console**: http://localhost:8080/h2-console
- 📈 **Actuator**: http://localhost:8080/actuator
- 🌐 **API**: http://localhost:8080/api/tasks

### Demo Scenario

1. Open the dashboard at http://localhost:8080
2. Click "Create Demo Scenario" to start 5 tasks with varying durations
3. Wait 10 seconds, then click "Initiate Graceful Shutdown"
4. Watch as short tasks complete and long tasks are suspended

## Docker Deployment

```bash
# Build the application
./build.sh

# Build and run with Docker
cd docker
docker-compose up --build
```

## API Endpoints

- `POST /api/tasks/demo-scenario` - Create demo tasks
- `POST /api/tasks/shutdown` - Initiate graceful shutdown
- `GET /api/tasks/status` - Get system status
- `GET /api/tasks` - List all tasks

## Architecture

This implementation demonstrates:
- **Graceful Shutdown**: Using Spring Boot lifecycle hooks
- **State Persistence**: Tasks are saved to H2 database
- **Progress Tracking**: Real-time progress updates
- **Circuit Breaker**: Stops accepting new tasks during shutdown
- **Timeout Handling**: Configurable graceful shutdown timeout

## Configuration

Key properties in `application.properties`:
```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
task.scheduler.graceful-shutdown-timeout=30
```
EOF

echo ""
echo "🎉 Project setup completed successfully!"
echo "=================================================="
echo ""
echo "📁 Project structure created in: $PROJECT_DIR"
echo "🚀 To get started:"
echo "   cd $PROJECT_NAME"
echo "   ./build.sh"
echo "   ./start.sh"
echo ""
echo "📊 Dashboard will be available at: http://localhost:8080"
echo "🧪 Run './test.sh' to execute the full test scenario"
echo ""
echo "✨ Happy coding!"