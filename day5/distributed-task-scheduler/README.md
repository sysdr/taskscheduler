# Distributed Task Scheduler - Day 5 Implementation

A hands-on implementation of distributed task scheduling concepts using Spring Boot, demonstrating high-level architecture components and their interactions.

## 🏗️ Architecture Overview

This project implements a distributed task scheduler with the following core components:

- **Scheduler Coordinator**: Discovers and distributes tasks across instances
- **Task Registry**: Persistent storage for task definitions and execution state
- **Distributed Lock Manager**: Prevents concurrent execution using Redis/ShedLock
- **Worker Nodes**: Execute tasks with proper isolation and monitoring
- **Event Stream**: Communication backbone for real-time coordination

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- Redis (for distributed locking)

### Running Locally
```bash
# Start Redis (if not already running)
redis-server

# Run the application
./start.sh
```

### Running with Docker
```bash
# Start all services (includes Redis and PostgreSQL)
./start-docker.sh
```

## 📊 Monitoring & Dashboards

- **Main Dashboard**: http://localhost:8080/scheduler/dashboard
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **H2 Console** (local): http://localhost:8080/h2-console

## 🎯 Key Features Demonstrated

1. **Race Condition Prevention**: Uses distributed locks to ensure single execution
2. **High Availability**: Multiple instances with automatic failover
3. **Task Visibility**: Complete monitoring of task states and executions
4. **Scalability**: Horizontal scaling with shared state management
5. **Fault Tolerance**: Graceful handling of failures and recovery

## 🧪 Testing the Distributed Behavior

1. Start two instances (using Docker compose or separate terminals)
2. Watch the dashboard to see which instance picks up tasks
3. Kill one instance and observe failover behavior
4. Check logs for distributed locking messages

## 📈 Performance Metrics

The system exposes the following key metrics:
- Active task count
- Running task count  
- Execution success/failure rates
- Average execution times
- Instance health status

## 🔧 Configuration

Key configuration options in `application.yml`:

```yaml
scheduler:
  enabled: true
  pool-size: 10
  leader-election:
    enabled: true
  distributed-lock:
    enabled: true
    default-lock-time: PT30M
```

## 📝 Sample Tasks

The system comes with pre-configured sample tasks:
- `daily-report`: Medium duration task (5s)
- `cleanup-temp-files`: Fast task (1s)  
- `data-backup`: Slow task (10s)

## 🐛 Troubleshooting

- **Redis connection issues**: Ensure Redis is running on localhost:6379
- **Duplicate executions**: Check that ShedLock is properly configured
- **Tasks not running**: Verify task definitions are in ACTIVE status

## 📚 Learning Objectives

This implementation demonstrates:
- Distributed system design patterns
- Race condition prevention techniques
- High availability architecture
- Monitoring and observability practices
- Spring Boot integration patterns

---

*Part of the "Hands-on System Design with Java Spring Boot" course series*
