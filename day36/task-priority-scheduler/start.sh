#!/bin/bash
set -e

echo "=== Starting Task Priority Scheduler System ==="

# Start RabbitMQ
echo "Starting RabbitMQ with Docker Compose..."
docker-compose up -d

echo "Waiting for RabbitMQ to be ready..."
sleep 10

# Start Spring Boot application
echo "Starting Spring Boot application..."
java -jar target/task-priority-scheduler-1.0.0.jar &
APP_PID=$!

echo "✓ System started successfully!"
echo ""
echo "Access points:"
echo "  - Dashboard: http://localhost:8080"
echo "  - RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo "  - Metrics: http://localhost:8080/actuator/metrics"
echo "  - Prometheus: http://localhost:8080/actuator/prometheus"
echo ""
echo "Application PID: $APP_PID"
echo "To stop: ./stop.sh"
