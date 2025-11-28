# Day 47: Web UI for Task Management

Modern web dashboard for the Task Scheduler system with real-time updates.

## Features

- **Real-time Updates**: Server-Sent Events (SSE) for live task status
- **Task Management**: Create, execute, and delete tasks
- **Metrics Dashboard**: Live statistics and success rates
- **Modern UI**: Dark theme with responsive design
- **Filtering**: View tasks by status (All, Running, Scheduled, Failed)

## Quick Start

### Without Docker

1. Build: `./build.sh`
2. Start: `./start.sh`
3. Test: `./test.sh`
4. Stop: `./stop.sh`

### With Docker

```bash
./build.sh
docker build -t scheduler-ui .
docker run -p 8080:8080 scheduler-ui
```

Or using docker-compose:

```bash
./build.sh
docker-compose up
```

## Access

- Dashboard: http://localhost:8080
- API: http://localhost:8080/api/tasks
- H2 Console: http://localhost:8080/h2-console

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 21
- **Frontend**: Vanilla JavaScript, CSS3
- **Database**: H2 (in-memory)
- **Real-time**: Server-Sent Events (SSE)

## API Endpoints

- GET `/api/tasks` - List all tasks
- POST `/api/tasks` - Create task
- DELETE `/api/tasks/{id}` - Delete task
- POST `/api/tasks/{id}/execute` - Execute task
- GET `/api/tasks/metrics` - Get metrics
- GET `/api/tasks/stream` - SSE stream

## Success Criteria

✅ Dashboard loads in <2 seconds
✅ Real-time updates work without refresh
✅ Tasks can be created and executed
✅ Responsive design works on tablet+
✅ Error handling with user-friendly messages

## Next Steps

Day 48: Add Spring Security for authentication and authorization
