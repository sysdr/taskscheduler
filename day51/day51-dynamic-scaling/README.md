# Day 51: Dynamic Scaling

## Quick Start
./build.sh && ./start.sh

Access:
- Dashboard: http://localhost:3000
- Metrics: http://localhost:8080/actuator/prometheus
- API: http://localhost:8080/api/metrics

## Features
- Auto-scaling based on queue depth
- Real-time metrics & visualization  
- Simulated traffic patterns
- AWS/GKE configuration templates

## Architecture
- Spring Boot 3.2 + Java 21 Virtual Threads
- Redis for task queue
- Micrometer/Prometheus metrics
- Target tracking auto-scaling policies

Stop: ./stop.sh
