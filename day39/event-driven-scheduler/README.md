# Event-Driven Task Scheduler

A production-ready event-driven task scheduling system built with Spring Boot and Apache Kafka.

## Features

- **Event-Driven Architecture**: Tasks triggered by events, not time schedules
- **Multiple Event Types**: File uploads, user actions, system health events
- **Kafka Integration**: High-throughput event processing
- **Dead Letter Queue**: Automatic handling of failed events
- **Real-time Dashboard**: Monitor events and tasks in real-time
- **Metrics & Monitoring**: Prometheus and Grafana integration
- **Virtual Threads**: Leverages Java 21 for high concurrency

## Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Compose

## Quick Start

1. **Build the project**:
   ```bash
   ./build.sh
   ```

2. **Start services**:
   ```bash
   ./start.sh
   ```

3. **Open dashboard**:
   - Application: http://localhost:8080
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)

4. **Test the system**:
   ```bash
   ./test.sh
   ```

5. **Stop services**:
   ```bash
   ./stop.sh
   ```

## Architecture

- **Event Sources**: Kafka topics for file, user, and system events
- **Event Listeners**: Spring Kafka listeners for each event type
- **Task Executor**: Asynchronous task processing with Virtual Threads
- **Persistence**: H2 database for task tracking
- **Monitoring**: Actuator + Prometheus + Grafana

## API Endpoints

### Publish Events
- `POST /api/events/file` - Publish file upload event
- `POST /api/events/user` - Publish user action event
- `POST /api/events/system` - Publish system health event

### Demo Endpoints
- `POST /api/events/demo/file-upload` - Simulate file upload
- `POST /api/events/demo/user-registration` - Simulate user registration
- `POST /api/events/demo/system-alert` - Simulate system alert

### Task Management
- `GET /api/tasks/recent` - Get recent tasks
- `GET /api/tasks/status/{status}` - Get tasks by status
- `GET /api/tasks/metrics` - Get system metrics
- `GET /api/tasks/stats` - Get task statistics

## Event Types

### File Upload Events
Triggers: Image processing, PDF extraction, metadata recording

### User Action Events
Triggers: Welcome emails, profile initialization, analytics

### System Health Events
Triggers: Cleanup tasks, archival, resource optimization

## Monitoring

The system exposes Prometheus metrics at `/actuator/prometheus`:
- Event counts by type
- Task execution metrics
- Success/failure rates
- Dead letter queue size

## Learn More

This is Day 39 of the "Hands-on System Design with Java Spring Boot" course.
For more lessons, visit the course repository.
