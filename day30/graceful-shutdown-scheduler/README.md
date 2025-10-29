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
