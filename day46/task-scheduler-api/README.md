# Task Scheduler REST API - Day 46

RESTful API for Task Management and Querying with comprehensive endpoints for task lifecycle management.

## Features

- ✅ Create, Read, Update, Delete task definitions
- ✅ Execute tasks manually (ad-hoc execution)
- ✅ Query execution history with filters
- ✅ Pause and resume tasks
- ✅ Get task statistics (success rate, avg duration)
- ✅ Pagination and filtering support
- ✅ Modern web dashboard
- ✅ Comprehensive error handling
- ✅ Virtual Threads for high concurrency

## Quick Start

### Without Docker
```bash
./build.sh
./start.sh
```

### With Docker
```bash
./build.sh
./start.sh --docker
```

## Access Points

- Dashboard: http://localhost:8080
- API: http://localhost:8080/api/v1
- H2 Console: http://localhost:8080/h2-console
- Actuator: http://localhost:8080/actuator

## API Endpoints

### Tasks
- `POST /api/v1/tasks` - Create task
- `GET /api/v1/tasks` - List tasks (with pagination/filtering)
- `GET /api/v1/tasks/{id}` - Get task details
- `PUT /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task
- `POST /api/v1/tasks/{id}/execute` - Execute task manually
- `POST /api/v1/tasks/{id}/pause` - Pause task
- `POST /api/v1/tasks/{id}/resume` - Resume task
- `GET /api/v1/tasks/{id}/statistics` - Get task statistics

### Executions
- `GET /api/v1/executions` - List executions (with filters)
- `GET /api/v1/executions/{id}` - Get execution details

## Example Usage

### Create a Task
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "daily-report",
    "description": "Generate daily report",
    "cronExpression": "0 0 1 * * ?",
    "maxRetries": 3,
    "timeoutSeconds": 300
  }'
```

### Execute Task Manually
```bash
curl -X POST http://localhost:8080/api/v1/tasks/1/execute
```

### Query Executions
```bash
curl "http://localhost:8080/api/v1/executions?taskId=1&status=SUCCESS&page=0&size=20"
```

### Get Task Statistics
```bash
curl http://localhost:8080/api/v1/tasks/1/statistics
```

## Testing

Run the test suite:
```bash
mvn test
```

## Stop Application

```bash
./stop.sh
```
