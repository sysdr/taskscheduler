# Day 54: Timezone-Aware Task Scheduler

A production-ready task scheduler that properly handles time zones and DST transitions.

## Features

- ✅ Global time zone support (400+ zones)
- ✅ Automatic DST transition handling
- ✅ Spring forward / Fall back edge cases
- ✅ Real-time dashboard with zone-aware displays
- ✅ Execution history with zone tracking
- ✅ DST warnings for upcoming transitions
- ✅ Java 21 Virtual Threads for scalability

## Quick Start

### Without Docker:
```bash
./build.sh
./start.sh
```

### With Docker:
```bash
docker-compose up --build
```

## Testing

```bash
./test.sh
```

## Dashboard

Access: http://localhost:8080

Features:
- Create tasks with timezone selection
- View next execution in both UTC and local time
- See DST status and warnings
- Track execution history across zones
- Real-time countdown to next task

## API Endpoints

- `POST /api/tasks` - Create task
- `GET /api/tasks` - List all tasks
- `GET /api/tasks/{id}` - Get task details
- `DELETE /api/tasks/{id}` - Delete task
- `GET /api/tasks/timezones` - List all time zones
- `GET /api/tasks/timezones/{id}/info` - Time zone info
- `GET /api/tasks/executions/recent` - Recent executions

## Configuration

Edit `src/main/resources/application.properties`:
- Scheduling frequency
- Database settings
- Virtual threads
- Logging levels

## Architecture Highlights

1. **Triple Storage**: Original time, UTC execution, recurrence rule
2. **DST Handler**: Automatic gap/overlap resolution
3. **Zone-Aware Scheduler**: Recalculates before each execution
4. **Execution Audit**: Full history with zone metadata

## Production Considerations

- Time zone database auto-updates via JDK
- Test across multiple years for DST changes
- Log both UTC and local times
- Monitor DST transition periods

## Technology Stack

- Java 21 (Virtual Threads)
- Spring Boot 3.2+
- H2 Database (in-memory)
- Modern responsive UI
- RESTful API

## Stop Application

```bash
./stop.sh
```
