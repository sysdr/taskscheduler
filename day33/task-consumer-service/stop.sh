#!/bin/bash

echo "🛑 Stopping Task Consumer Service..."

# Stop the Spring Boot application
echo "🔌 Stopping Spring Boot application..."
pkill -f "task-consumer-service"

# Stop RabbitMQ Docker container
if command -v docker &> /dev/null; then
    if docker ps | grep -q "rabbitmq-taskscheduler"; then
        echo "🐰 Stopping RabbitMQ container..."
        docker stop rabbitmq-taskscheduler
        docker rm rabbitmq-taskscheduler
    fi
fi

echo "✅ Services stopped successfully!"
