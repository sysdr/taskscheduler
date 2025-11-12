# Day 37: Backpressure Task Scheduler

## Overview
Complete implementation of backpressure handling with rate limiting for task scheduler.

## Features
- Token bucket rate limiting (10 tasks/sec)
- Real-time metrics and monitoring
- Interactive dashboard with burst testing
- Kafka-based message queue
- Dynamic rate limiter control

## Quick Start

### With Docker:
```bash
./start.sh
```

### Without Docker (requires local Kafka):
```bash
mvn spring-boot:run
```

### Stop:
```bash
./stop.sh
```

## Access
- Dashboard: http://localhost:8080
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## Testing
1. Open dashboard
2. Submit single tasks (observe normal rate)
3. Submit burst (1000+ tasks)
4. Watch rate limiting in action
5. Toggle rate limiter to compare behavior
