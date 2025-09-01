#!/bin/bash

echo "🛑 Stopping Task Scheduler"
echo "========================="

# Stop running Spring Boot application
echo "Stopping Spring Boot application..."
pkill -f "task-scheduler-distributed-locks" || true

# Stop Docker services if running
if command -v docker &> /dev/null && docker info &> /dev/null; then
    echo "Stopping Docker services..."
    cd docker 2>/dev/null
    docker-compose down 2>/dev/null || true
fi

echo "✅ All services stopped"
