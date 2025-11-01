#!/bin/bash

echo "🛑 Stopping Task Scheduler Message Queue System..."

# Stop Spring Boot application
echo "🍃 Stopping Spring Boot application..."
pkill -f "task-scheduler-mq-1.0.0.jar" 2>/dev/null || echo "   Spring Boot app was not running"

# Stop Docker services
echo "🐳 Stopping Docker services..."
cd docker && docker-compose down

echo "✅ System stopped successfully!"
