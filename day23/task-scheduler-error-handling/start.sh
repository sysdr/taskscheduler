#!/bin/bash

echo "🚀 Starting Task Scheduler Error Handling Implementation..."

# Check if running with Docker
if [ "$1" = "docker" ]; then
    echo "🐳 Starting with Docker..."
    cd docker
    docker-compose up -d
    echo "✅ Application started with Docker!"
    echo "📱 Dashboard: http://localhost:8080"
    echo "🗄️ H2 Console: http://localhost:8080/h2-console"
    echo "📊 Health Check: http://localhost:8080/actuator/health"
else
    echo "☕ Starting with Java..."
    ./mvnw spring-boot:run
fi
