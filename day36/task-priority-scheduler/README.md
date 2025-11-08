# Day 36: Task Prioritization with Message Queues

## Overview
This implementation demonstrates a multi-queue priority system with:
- Three priority levels (HIGH, NORMAL, LOW)
- Separate RabbitMQ queues per priority
- Starvation prevention mechanism
- Real-time metrics and monitoring
- Modern web dashboard

## Quick Start

### With Docker (Recommended)
```bash
./build.sh   # Build the application
./start.sh   # Start everything
```

### Without Docker
1. Install and start RabbitMQ manually
2. Run: `./build.sh && java -jar target/task-priority-scheduler-1.0.0.jar`

## Access
- Dashboard: http://localhost:8080
- RabbitMQ UI: http://localhost:15672 (guest/guest)
- Metrics: http://localhost:8080/actuator/metrics

## Testing Priority System
1. Submit 1000 LOW priority tasks
2. Submit 100 HIGH priority tasks
3. Observe HIGH tasks complete first despite being submitted later
4. Monitor starvation prevention in logs

## Stopping
```bash
./stop.sh
```
