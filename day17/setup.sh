#!/bin/bash

# Database-Backed Leader Election Implementation Script
# Day 17: Implementing Database-Backed Leader Election

set -e  # Exit on any error

PROJECT_NAME="task-scheduler-leader-election"
BASE_DIR="$(pwd)/$PROJECT_NAME"
MYSQL_ROOT_PASSWORD="rootpassword"
MYSQL_DATABASE="task_scheduler"
MYSQL_USER="scheduler"
MYSQL_PASSWORD="schedulerpass"

echo "=== Creating Ultra-Scalable Task Scheduler Leader Election Project ==="
echo "Project: $PROJECT_NAME"
echo "Base Directory: $BASE_DIR"

# Create project structure
echo "Creating project directory structure..."
mkdir -p "$BASE_DIR"
cd "$BASE_DIR"

mkdir -p src/main/java/com/taskscheduler/{config,service,model,controller,repository}
mkdir -p src/main/resources
mkdir -p src/test/java/com/taskscheduler/service
mkdir -p src/test/resources
mkdir -p docker
mkdir -p scripts

# Create pom.xml
echo "Generating pom.xml..."
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.taskscheduler</groupId>
    <artifactId>leader-election</artifactId>
    <version>1.0.0</version>
    <name>Task Scheduler Leader Election</name>
    <description>Database-backed leader election for ultra-scalable task scheduler</description>

    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
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
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
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
echo "Creating application configuration..."
cat > src/main/resources/application.properties << 'EOF'
# Application Configuration
spring.application.name=task-scheduler-leader-election
server.port=8080
management.endpoints.web.exposure.include=health,info,metrics,prometheus

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/task_scheduler
spring.datasource.username=scheduler
spring.datasource.password=schedulerpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool Configuration
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.pool-name=HikariCP

# Leader Election Configuration
leader.election.service-name=task-scheduler
leader.election.heartbeat-interval-ms=5000
leader.election.lease-duration-ms=15000

# Logging
logging.level.com.taskscheduler=DEBUG
logging.level.org.springframework.jdbc=DEBUG
EOF

# Create test application.properties
cat > src/test/resources/application.properties << 'EOF'
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
leader.election.heartbeat-interval-ms=1000
leader.election.lease-duration-ms=3000
EOF

# Create Main Application Class
echo "Creating main application class..."
cat > src/main/java/com/taskscheduler/TaskSchedulerApplication.java << 'EOF'
package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApplication.class, args);
    }
}
EOF

# Create Leader Election Entity
echo "Creating Leader Election entity..."
cat > src/main/java/com/taskscheduler/model/LeaderElection.java << 'EOF'
package com.taskscheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "leader_election")
public class LeaderElection {
    
    @Id
    @Column(name = "service_name", length = 100)
    private String serviceName;
    
    @NotNull
    @Column(name = "leader_instance_id")
    private String leaderInstanceId;
    
    @NotNull
    @Column(name = "lease_expires_at")
    private LocalDateTime leaseExpiresAt;
    
    @NotNull
    @Column(name = "heartbeat_interval_ms")
    private Integer heartbeatIntervalMs;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public LeaderElection() {}
    
    public LeaderElection(String serviceName, String leaderInstanceId, 
                         LocalDateTime leaseExpiresAt, Integer heartbeatIntervalMs) {
        this.serviceName = serviceName;
        this.leaderInstanceId = leaderInstanceId;
        this.leaseExpiresAt = leaseExpiresAt;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getLeaderInstanceId() { return leaderInstanceId; }
    public void setLeaderInstanceId(String leaderInstanceId) { this.leaderInstanceId = leaderInstanceId; }
    
    public LocalDateTime getLeaseExpiresAt() { return leaseExpiresAt; }
    public void setLeaseExpiresAt(LocalDateTime leaseExpiresAt) { this.leaseExpiresAt = leaseExpiresAt; }
    
    public Integer getHeartbeatIntervalMs() { return heartbeatIntervalMs; }
    public void setHeartbeatIntervalMs(Integer heartbeatIntervalMs) { this.heartbeatIntervalMs = heartbeatIntervalMs; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
EOF

# Create Repository
echo "Creating Leader Election repository..."
cat > src/main/java/com/taskscheduler/repository/LeaderElectionRepository.java << 'EOF'
package com.taskscheduler.repository;

import com.taskscheduler.model.LeaderElection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LeaderElectionRepository extends JpaRepository<LeaderElection, String> {
    
    @Query("SELECT le FROM LeaderElection le WHERE le.serviceName = :serviceName")
    Optional<LeaderElection> findByServiceName(@Param("serviceName") String serviceName);
    
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO leader_election (service_name, leader_instance_id, lease_expires_at, heartbeat_interval_ms, created_at, updated_at)
        VALUES (:serviceName, :instanceId, :expiresAt, :heartbeatInterval, :now, :now)
        ON DUPLICATE KEY UPDATE
            leader_instance_id = CASE 
                WHEN lease_expires_at < :now THEN :instanceId
                ELSE leader_instance_id
            END,
            lease_expires_at = CASE 
                WHEN lease_expires_at < :now OR leader_instance_id = :instanceId 
                THEN :expiresAt
                ELSE lease_expires_at
            END,
            updated_at = :now
        """, nativeQuery = true)
    int tryAcquireOrRenewLease(@Param("serviceName") String serviceName,
                              @Param("instanceId") String instanceId,
                              @Param("expiresAt") LocalDateTime expiresAt,
                              @Param("heartbeatInterval") Integer heartbeatInterval,
                              @Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM LeaderElection le WHERE le.serviceName = :serviceName AND le.leaderInstanceId = :instanceId")
    int releaseLease(@Param("serviceName") String serviceName, @Param("instanceId") String instanceId);
}
EOF

# Create Leader Election Service
echo "Creating Leader Election service..."
cat > src/main/java/com/taskscheduler/service/LeaderElectionService.java << 'EOF'
package com.taskscheduler.service;

import com.taskscheduler.model.LeaderElection;
import com.taskscheduler.repository.LeaderElectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class LeaderElectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionService.class);
    
    private final LeaderElectionRepository repository;
    private final String serviceName;
    private final int heartbeatIntervalMs;
    private final int leaseDurationMs;
    private final String instanceId;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    
    public LeaderElectionService(LeaderElectionRepository repository,
                               @Value("${leader.election.service-name:task-scheduler}") String serviceName,
                               @Value("${leader.election.heartbeat-interval-ms:5000}") int heartbeatIntervalMs,
                               @Value("${leader.election.lease-duration-ms:15000}") int leaseDurationMs) {
        this.repository = repository;
        this.serviceName = serviceName;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.leaseDurationMs = leaseDurationMs;
        this.instanceId = generateInstanceId();
    }
    
    @PostConstruct
    public void initialize() {
        logger.info("Initializing Leader Election Service for service: {}, instance: {}", 
                   serviceName, instanceId);
        logger.info("Heartbeat interval: {}ms, Lease duration: {}ms", 
                   heartbeatIntervalMs, leaseDurationMs);
    }
    
    @Scheduled(fixedDelayString = "${leader.election.heartbeat-interval-ms:5000}")
    public void maintainLeadership() {
        if (!isRunning.get()) {
            return;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusNanos(leaseDurationMs * 1_000_000L);
            
            int result = repository.tryAcquireOrRenewLease(
                serviceName, instanceId, expiresAt, heartbeatIntervalMs, now);
            
            // Check if we successfully acquired/renewed the lease
            boolean newLeaderStatus = checkLeadershipStatus();
            
            if (newLeaderStatus && !isLeader.get()) {
                logger.info("✅ Acquired leadership for service: {}, instance: {}", serviceName, instanceId);
                isLeader.set(true);
            } else if (!newLeaderStatus && isLeader.get()) {
                logger.warn("❌ Lost leadership for service: {}, instance: {}", serviceName, instanceId);
                isLeader.set(false);
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Leadership maintenance - Service: {}, Instance: {}, IsLeader: {}", 
                           serviceName, instanceId, isLeader.get());
            }
            
        } catch (Exception e) {
            logger.error("Error during leadership maintenance for instance: " + instanceId, e);
            isLeader.set(false);
        }
    }
    
    private boolean checkLeadershipStatus() {
        try {
            Optional<LeaderElection> currentLeader = repository.findByServiceName(serviceName);
            if (currentLeader.isPresent()) {
                LeaderElection leader = currentLeader.get();
                LocalDateTime now = LocalDateTime.now();
                
                // Check if lease is not expired and we are the leader
                return leader.getLeaseExpiresAt().isAfter(now) && 
                       instanceId.equals(leader.getLeaderInstanceId());
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking leadership status", e);
            return false;
        }
    }
    
    public boolean isLeader() {
        return isLeader.get() && isRunning.get();
    }
    
    public String getCurrentLeader() {
        try {
            Optional<LeaderElection> currentLeader = repository.findByServiceName(serviceName);
            if (currentLeader.isPresent()) {
                LeaderElection leader = currentLeader.get();
                LocalDateTime now = LocalDateTime.now();
                
                if (leader.getLeaseExpiresAt().isAfter(now)) {
                    return leader.getLeaderInstanceId();
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting current leader", e);
            return null;
        }
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down Leader Election Service for instance: {}", instanceId);
        isRunning.set(false);
        
        if (isLeader.get()) {
            try {
                repository.releaseLease(serviceName, instanceId);
                logger.info("Released leadership lease for instance: {}", instanceId);
            } catch (Exception e) {
                logger.error("Error releasing leadership lease", e);
            }
        }
        
        isLeader.set(false);
    }
    
    private String generateInstanceId() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            long timestamp = System.currentTimeMillis();
            return hostname + "-" + timestamp + "-" + Thread.currentThread().getId();
        } catch (Exception e) {
            logger.warn("Could not determine hostname, using fallback instance ID", e);
            return "instance-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
        }
    }
}
EOF

# Create Task Processor Service
echo "Creating Task Processor service..."
cat > src/main/java/com/taskscheduler/service/TaskProcessorService.java << 'EOF'
package com.taskscheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorService.class);
    
    private final LeaderElectionService leaderElectionService;
    private final AtomicLong taskCounter = new AtomicLong(0);
    
    public TaskProcessorService(LeaderElectionService leaderElectionService) {
        this.leaderElectionService = leaderElectionService;
    }
    
    @Scheduled(fixedDelay = 3000) // Process tasks every 3 seconds
    public void processTasks() {
        if (leaderElectionService.isLeader()) {
            long taskId = taskCounter.incrementAndGet();
            logger.info("🚀 Processing task #{} at {} (Leader: {})", 
                       taskId, LocalDateTime.now(), leaderElectionService.getInstanceId());
            
            // Simulate task processing
            try {
                Thread.sleep(500); // Simulate work
                logger.info("✅ Completed task #{}", taskId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Task #{} interrupted", taskId);
            }
        } else {
            logger.debug("⏸️ Not leader, skipping task processing (Current leader: {})", 
                        leaderElectionService.getCurrentLeader());
        }
    }
    
    public long getProcessedTaskCount() {
        return taskCounter.get();
    }
}
EOF

# Create Status Controller
echo "Creating Status controller..."
cat > src/main/java/com/taskscheduler/controller/StatusController.java << 'EOF'
package com.taskscheduler.controller;

import com.taskscheduler.service.LeaderElectionService;
import com.taskscheduler.service.TaskProcessorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {
    
    private final LeaderElectionService leaderElectionService;
    private final TaskProcessorService taskProcessorService;
    
    public StatusController(LeaderElectionService leaderElectionService, 
                          TaskProcessorService taskProcessorService) {
        this.leaderElectionService = leaderElectionService;
        this.taskProcessorService = taskProcessorService;
    }
    
    @GetMapping
    public Map<String, Object> getStatus() {
        return Map.of(
            "instanceId", leaderElectionService.getInstanceId(),
            "isLeader", leaderElectionService.isLeader(),
            "currentLeader", leaderElectionService.getCurrentLeader(),
            "processedTasks", taskProcessorService.getProcessedTaskCount()
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> getHealth() {
        return Map.of(
            "status", "UP",
            "instance", leaderElectionService.getInstanceId()
        );
    }
}
EOF

# Create test file
echo "Creating test file..."
cat > src/test/java/com/taskscheduler/service/LeaderElectionServiceTest.java << 'EOF'
package com.taskscheduler.service;

import com.taskscheduler.repository.LeaderElectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class LeaderElectionServiceTest {
    
    @Autowired
    private LeaderElectionService leaderElectionService;
    
    @Autowired
    private LeaderElectionRepository repository;
    
    @Test
    public void testInstanceIdGeneration() {
        String instanceId = leaderElectionService.getInstanceId();
        assertNotNull(instanceId);
        assertTrue(instanceId.length() > 10);
    }
    
    @Test
    public void testLeaderElectionInitialization() {
        assertNotNull(leaderElectionService);
        // Initially should not be leader (leadership acquired via scheduled tasks)
        assertFalse(leaderElectionService.isLeader());
    }
}
EOF

# Create Docker Compose
echo "Creating Docker Compose configuration..."
cat > docker/docker-compose.yml << 'EOF'
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: task-scheduler-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: task_scheduler
      MYSQL_USER: scheduler
      MYSQL_PASSWORD: schedulerpass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - task-scheduler-network

  app1:
    build: ..
    container_name: task-scheduler-app1
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/task_scheduler
      - SERVER_PORT=8080
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - task-scheduler-network

  app2:
    build: ..
    container_name: task-scheduler-app2
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/task_scheduler
      - SERVER_PORT=8081
    ports:
      - "8081:8081"
    depends_on:
      - mysql
    networks:
      - task-scheduler-network

networks:
  task-scheduler-network:
    driver: bridge

volumes:
  mysql_data:
EOF

# Create database initialization script
cat > docker/init.sql << 'EOF'
CREATE DATABASE IF NOT EXISTS task_scheduler;
USE task_scheduler;

CREATE TABLE IF NOT EXISTS leader_election (
    service_name VARCHAR(100) PRIMARY KEY,
    leader_instance_id VARCHAR(255) NOT NULL,
    lease_expires_at TIMESTAMP(6) NOT NULL,
    heartbeat_interval_ms INT NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

-- Create index for performance
CREATE INDEX idx_lease_expires_at ON leader_election(lease_expires_at);
EOF

# Create Dockerfile
echo "Creating Dockerfile..."
cat > Dockerfile << 'EOF'
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/leader-election-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Create build script
echo "Creating build.sh..."
cat > build.sh << 'EOF'
#!/bin/bash

set -e

echo "=== Building Task Scheduler Leader Election Project ==="

# Install MySQL if not running in Docker
if ! command -v mysql &> /dev/null; then
    echo "Installing MySQL..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        brew install mysql
        brew services start mysql
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        sudo apt-get update
        sudo apt-get install -y mysql-server
        sudo systemctl start mysql
    fi
fi

# Build the project
echo "Building with Maven..."
./mvnw clean package -DskipTests

# Run tests
echo "Running tests..."
./mvnw test

# Start MySQL with Docker
echo "Starting MySQL with Docker..."
cd docker
docker-compose up -d mysql

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
sleep 10

echo "✅ Build completed successfully!"
echo "Use './start.sh' to start the application"
echo "Use './stop.sh' to stop all services"
EOF

# Create start script
echo "Creating start.sh..."
cat > start.sh << 'EOF'
#!/bin/bash

set -e

echo "=== Starting Task Scheduler Leader Election Services ==="

# Start MySQL if not running
cd docker
docker-compose up -d mysql

# Wait for MySQL
echo "Waiting for MySQL to start..."
sleep 10

# Go back to project root
cd ..

# Start first instance in background
echo "Starting Application Instance 1 on port 8080..."
SERVER_PORT=8080 ./mvnw spring-boot:run &
APP1_PID=$!

# Wait a bit before starting second instance
sleep 5

# Start second instance in background
echo "Starting Application Instance 2 on port 8081..."
SERVER_PORT=8081 ./mvnw spring-boot:run &
APP2_PID=$!

# Save PIDs for cleanup
echo $APP1_PID > .app1.pid
echo $APP2_PID > .app2.pid

echo "✅ Both instances started!"
echo "Instance 1: http://localhost:8080/api/status"
echo "Instance 2: http://localhost:8081/api/status"
echo "API Documentation: http://localhost:8080/swagger-ui.html"
echo ""
echo "Monitor leadership by checking status endpoints:"
echo "curl http://localhost:8080/api/status"
echo "curl http://localhost:8081/api/status"
echo ""
echo "Use './stop.sh' to stop all services"

wait
EOF

# Create stop script
echo "Creating stop.sh..."
cat > stop.sh << 'EOF'
#!/bin/bash

echo "=== Stopping Task Scheduler Leader Election Services ==="

# Kill application instances
if [ -f .app1.pid ]; then
    APP1_PID=$(cat .app1.pid)
    echo "Stopping Application Instance 1 (PID: $APP1_PID)..."
    kill $APP1_PID 2>/dev/null || true
    rm .app1.pid
fi

if [ -f .app2.pid ]; then
    APP2_PID=$(cat .app2.pid)
    echo "Stopping Application Instance 2 (PID: $APP2_PID)..."
    kill $APP2_PID 2>/dev/null || true
    rm .app2.pid
fi

# Stop Docker containers
echo "Stopping MySQL container..."
cd docker
docker-compose down

echo "✅ All services stopped!"
EOF

# Create Maven wrapper if not exists
echo "Creating Maven wrapper..."
if [ ! -f mvnw ]; then
    mvn wrapper:wrapper
fi

# Make scripts executable
chmod +x build.sh start.sh stop.sh mvnw

# Create demo script
echo "Creating demo.sh..."
cat > demo.sh << 'EOF'
#!/bin/bash

set -e

echo "=== Task Scheduler Leader Election Demo ==="

echo "Starting services..."
./start.sh &
SERVICES_PID=$!

# Wait for services to start
echo "Waiting for services to initialize..."
sleep 20

echo ""
echo "=== Checking Service Status ==="
echo "Instance 1 Status:"
curl -s http://localhost:8080/api/status | jq '.'

echo ""
echo "Instance 2 Status:"
curl -s http://localhost:8081/api/status | jq '.'

echo ""
echo "=== Monitoring Leader Election (30 seconds) ==="
for i in {1..10}; do
    echo "--- Check $i ---"
    echo "Instance 1 - IsLeader: $(curl -s http://localhost:8080/api/status | jq -r '.isLeader')"
    echo "Instance 2 - IsLeader: $(curl -s http://localhost:8081/api/status | jq -r '.isLeader')"
    sleep 3
done

echo ""
echo "=== Testing Leader Failover ==="
echo "Killing current leader to test failover..."

# Find and kill current leader
LEADER_1=$(curl -s http://localhost:8080/api/status | jq -r '.isLeader')
if [ "$LEADER_1" = "true" ]; then
    echo "Instance 1 is leader, killing it..."
    APP1_PID=$(cat .app1.pid)
    kill $APP1_PID
    rm .app1.pid
else
    echo "Instance 2 is leader, killing it..."
    APP2_PID=$(cat .app2.pid)
    kill $APP2_PID
    rm .app2.pid
fi

echo "Waiting for failover..."
sleep 10

echo "Checking new leadership status:"
if [ -f .app1.pid ]; then
    echo "Remaining Instance 1 - IsLeader: $(curl -s http://localhost:8080/api/status | jq -r '.isLeader')"
fi
if [ -f .app2.pid ]; then
    echo "Remaining Instance 2 - IsLeader: $(curl -s http://localhost:8081/api/status | jq -r '.isLeader')"
fi

echo ""
echo "✅ Demo completed! Check logs for detailed leader election behavior."
echo "Use './stop.sh' to clean up remaining services."

# Clean up
kill $SERVICES_PID 2>/dev/null || true
EOF

chmod +x demo.sh

echo ""
echo "✅ Project structure created successfully!"
echo ""
echo "Directory structure:"
find . -type f -name "*.java" -o -name "*.properties" -o -name "*.xml" -o -name "*.yml" -o -name "*.sh" | head -20
echo ""
echo "Next steps:"
echo "1. Run './build.sh' to build the project"
echo "2. Run './start.sh' to start the services"
echo "3. Run './demo.sh' for a complete demonstration"
echo "4. Run './stop.sh' to stop all services"
echo ""
echo "API Endpoints:"
echo "- Status: http://localhost:8080/api/status"
echo "- Health: http://localhost:8080/api/status/health" 
echo "- Swagger UI: http://localhost:8080/swagger-ui.html"