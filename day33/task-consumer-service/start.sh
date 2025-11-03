#!/bin/bash

echo "🚀 Starting Task Consumer Service..."

# Function to check if a port is in use
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        return 0
    else
        return 1
    fi
}

# Check if RabbitMQ is running
if check_port 5672; then
    echo "✅ RabbitMQ is already running on port 5672"
else
    echo "🐰 Starting RabbitMQ with Docker..."
    if command -v docker &> /dev/null; then
        docker run -d --name rabbitmq-taskscheduler \
            -p 5672:5672 \
            -p 15672:15672 \
            -e RABBITMQ_DEFAULT_USER=guest \
            -e RABBITMQ_DEFAULT_PASS=guest \
            rabbitmq:3-management
        
        echo "⏳ Waiting for RabbitMQ to start..."
        sleep 15
    else
        echo "❌ Docker not found and RabbitMQ not running. Please start RabbitMQ manually."
        exit 1
    fi
fi

# Start the application
if [ -f "target/task-consumer-service-1.0.0.jar" ]; then
    echo "🎯 Starting application..."
    java -jar target/task-consumer-service-1.0.0.jar
else
    echo "❌ JAR file not found. Please run build.sh first."
    exit 1
fi
