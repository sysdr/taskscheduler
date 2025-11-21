# Day 43: Task Scheduler with Prometheus & Grafana Monitoring

## Overview
Production-ready task scheduler with comprehensive monitoring using Prometheus and Grafana.

## Architecture
- **Spring Boot 3.2**: Core application with Virtual Threads
- **Micrometer**: Metrics instrumentation
- **Prometheus**: Time-series metrics collection
- **Grafana**: Real-time visualization dashboards
- **H2 Database**: Task persistence

## Quick Start

### Build
```bash
./build.sh
```

### Start All Services
```bash
./start.sh
```

### Run Tests
```bash
./test.sh
```

### Stop All Services
```bash
./stop.sh
```

## Access Points
- **Application**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Metrics Endpoint**: http://localhost:8080/actuator/prometheus

## Key Metrics
- `task_submitted_total`: Total tasks submitted
- `task_completed_total`: Successfully completed tasks
- `task_failed_total`: Failed tasks
- `task_active_count`: Currently executing tasks
- `task_queue_depth`: Tasks waiting in queue
- `task_execution_duration`: Task duration with percentiles

## API Endpoints
- `POST /api/tasks`: Submit new task
- `GET /api/tasks`: List all tasks
- `GET /api/tasks/{id}`: Get task details
- `GET /api/tasks/stats`: Get statistics

## Grafana Dashboard
The pre-configured dashboard includes:
1. Total tasks submitted (stat)
2. Active tasks (stat)
3. Queue depth (stat)
4. Success rate gauge
5. Task execution rate (timeseries)
6. Duration percentiles (timeseries)

## Customization
- Edit `prometheus/prometheus.yml` for scrape configs
- Modify `grafana/dashboards/task-scheduler.json` for dashboard changes
- Adjust `application.yml` for application settings

## Troubleshooting
- If Prometheus can't scrape: Check `host.docker.internal` resolves correctly
- If Grafana shows no data: Verify Prometheus datasource connection
- If metrics don't appear: Ensure application is running and accessible

## Learning Objectives
✅ Understand Prometheus scraping architecture
✅ Configure Spring Boot Actuator for Prometheus
✅ Design effective monitoring dashboards
✅ Query metrics with PromQL
✅ Visualize time-series data in Grafana
