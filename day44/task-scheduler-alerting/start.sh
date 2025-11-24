#!/bin/bash

echo "Starting Task Scheduler Alerting System..."

# Start Docker services
docker compose up -d

echo "Waiting for services to be ready..."
sleep 10

# Start Spring Boot application
java -jar target/task-scheduler-alerting-1.0.0.jar &

echo "✓ All services started!"
echo ""
echo "Access points:"
echo "  - Application:  http://localhost:8084"
echo "  - Dashboard:    http://localhost:8084/index.html"
echo "  - Prometheus:   http://localhost:9090"
echo "  - AlertManager: http://localhost:9093"
echo "  - Grafana:      http://localhost:3000 (admin/admin)"
