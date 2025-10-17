# Spring Retry Task Scheduler

A comprehensive implementation of advanced retry mechanisms using Spring Retry framework for ultra-scalable task scheduling systems.

## 🚀 Features

- **Declarative Retry Policies**: Annotation-driven retry configuration
- **Multiple Backoff Strategies**: Fixed, exponential, and random jitter
- **Exception Classification**: Transient vs permanent failure handling
- **Recovery Mechanisms**: Graceful degradation when retries are exhausted
- **Metrics Integration**: Comprehensive monitoring with Micrometer
- **Dead Letter Queue Support**: Failed tasks move to DLQ for analysis
- **REST API**: Full CRUD operations for task management
- **Docker Support**: Containerized deployment with monitoring stack

## 🏗️ Architecture

```
Task Creation → Task Execution Service → [Spring Retry Proxy] → Service Layer
                      ↓                        ↓
                 Database Storage         Retry Logic + Backoff
                      ↓                        ↓
              Status Tracking            Recovery/Dead Letter
```

## 🛠️ Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- Docker & Docker Compose

### Build and Run

```bash
# Build the project
./scripts/build.sh

# Start all services
./scripts/start.sh

# Run demo
./scripts/demo.sh

# Stop services
./scripts/stop.sh
```

## 📊 Monitoring

- **Application**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console (sa/password)
- **Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## 🔧 Configuration

Retry policies are configurable via `application.yml`:

```yaml
app:
  retry:
    email-service:
      max-attempts: 3
      initial-delay: 1000
      multiplier: 2.0
      max-delay: 5000
```

## 📝 API Examples

### Create a Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Email Notification",
    "description": "Send welcome email",
    "type": "EMAIL_NOTIFICATION",
    "maxRetries": 3
  }'
```

### Execute a Task
```bash
curl -X POST http://localhost:8080/api/tasks/1/execute
```

### Get Task Status
```bash
curl http://localhost:8080/api/tasks/1
```

## 🧪 Testing

```bash
mvn test
```

## 📈 Key Metrics

- `tasks.started` - Total tasks started
- `tasks.completed` - Successfully completed tasks
- `tasks.failed` - Failed tasks
- `tasks.retried` - Retry attempts
- `tasks.execution.time` - Execution duration

## 🎯 Learning Objectives

This implementation demonstrates:

1. **Spring Retry Integration**: Declarative retry policies with annotations
2. **Exception Handling**: Proper classification of transient vs permanent failures
3. **Backoff Strategies**: Exponential backoff with jitter for system stability
4. **Recovery Patterns**: Graceful degradation when retries are exhausted
5. **Observability**: Comprehensive metrics and monitoring
6. **Production Readiness**: Docker deployment with monitoring stack

## 🔄 Retry Flow

1. **Task Execution** → Success or Exception
2. **Exception Classification** → Retryable or Permanent
3. **Retry Decision** → Check attempts remaining
4. **Backoff Application** → Wait with exponential delay
5. **Retry Attempt** → Execute again
6. **Recovery/Failure** → Graceful degradation or DLQ

## 🎓 Assignment Solution

The implementation includes:
- ✅ Email notification service with retry logic
- ✅ 3 retry attempts with exponential backoff
- ✅ Exception classification (transient vs permanent)
- ✅ Recovery method for failed tasks
- ✅ Comprehensive metrics tracking
- ✅ Dead letter queue preparation

## 📚 Next Steps

Tomorrow's lesson will cover **Dead Letter Queues** - handling tasks that have exhausted all retry attempts and require manual intervention or delayed reprocessing.
