# Day 42: Monitoring Task Health with Micrometer/Actuator

## Quick Start

```bash
# Build
./build.sh

# Start
./start.sh

# Test
./test.sh

# Stop
./stop.sh
```

## Endpoints

### Dashboard
- **http://localhost:8080** - Real-time metrics dashboard

### Actuator
- `/actuator/health` - Health status
- `/actuator/metrics` - Available metrics
- `/actuator/metrics/{name}` - Specific metric
- `/actuator/prometheus` - Prometheus format

### API
- `POST /api/tasks` - Submit task
- `GET /api/tasks` - List tasks
- `GET /api/tasks/stats` - Statistics
- `POST /api/tasks/generate/{count}` - Generate test tasks
- `GET /api/metrics/summary` - Metrics summary
- `GET /api/metrics/timers` - Timer details

## Key Metrics

- `task.submitted.total` - Counter of submitted tasks
- `task.completed.total` - Counter of completed tasks
- `task.failed.total` - Counter of failed tasks
- `task.active.count` - Gauge of executing tasks
- `task.queued.count` - Gauge of queued tasks
- `task.execution` - Timer with latency percentiles

## Docker

```bash
docker-compose up --build
```
