# Day 40: Scheduler as a Service

A multi-tenant task scheduling platform demonstrating how to build a scheduler that serves multiple microservices.

## Architecture

- **Scheduler Service** (Port 8080): Central scheduling service with JWT authentication
- **Payment Service** (Port 8081): Example tenant submitting payment reconciliation tasks
- **Notification Service** (Port 8082): Example tenant submitting batch notification tasks
- **Dashboard** (Port 8083): Web UI for monitoring tasks across all tenants

## Features

- Multi-tenant task isolation
- JWT-based authentication
- RESTful API for task submission
- Priority-based task scheduling
- Webhook callbacks
- Per-tenant metrics and monitoring
- Modern web dashboard

## Quick Start

### Without Docker

1. Build all services:
```bash
./scripts/build.sh
```

2. Start all services:
```bash
./scripts/start.sh
```

3. Open dashboard:
```
http://localhost:8083
```

4. Stop all services:
```bash
./scripts/stop.sh
```

### With Docker

1. Build and start:
```bash
docker-compose up --build
```

2. Open dashboard:
```
http://localhost:8083
```

## Testing

Access the dashboard at http://localhost:8083

1. Register a new tenant
2. Copy the API key
3. Submit a task
4. Monitor task execution in real-time
5. View metrics across all tasks

## API Endpoints

### Authentication
- POST `/api/v1/auth/register` - Register new tenant
- POST `/api/v1/auth/token` - Get JWT token

### Tasks
- POST `/api/v1/tasks` - Submit task
- GET `/api/v1/tasks/{taskId}` - Get task status
- GET `/api/v1/tasks?status=RUNNING` - Query tasks
- DELETE `/api/v1/tasks/{taskId}` - Cancel task

### Metrics
- GET `/api/v1/metrics` - Get per-tenant metrics

## Success Criteria

✅ Multiple services can submit tasks through unified API
✅ Tenant isolation prevents cross-tenant data access
✅ JWT authentication secures all endpoints
✅ Tasks execute with priority-based scheduling
✅ Webhooks notify services of task completion
✅ Dashboard shows real-time metrics per tenant
