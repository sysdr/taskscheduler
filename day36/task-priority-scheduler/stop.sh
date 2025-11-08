#!/bin/bash

echo "=== Stopping Task Priority Scheduler System ==="

# Stop Spring Boot
echo "Stopping Spring Boot application..."
pkill -f "task-priority-scheduler-1.0.0.jar" || echo "Application not running"

# Stop RabbitMQ
echo "Stopping RabbitMQ..."
docker-compose down

echo "✓ System stopped successfully!"
