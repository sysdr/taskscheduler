#!/bin/bash

echo "🐳 Starting Task Scheduler with Docker"
echo "====================================="

# Build the application
echo "🔧 Building application..."
mvn clean package -DskipTests

# Build and start Docker containers
echo "🐳 Building and starting Docker containers..."
docker-compose up --build -d

echo "⏳ Waiting for application to start..."
sleep 20

# Check if application is running
if curl -f http://localhost:8080/actuator/health &> /dev/null; then
    echo "✅ Application started successfully with Docker!"
    echo "📊 Dashboard: http://localhost:8080"
    echo "🐳 Container status: docker-compose ps"
    echo "📋 Container logs: docker-compose logs -f"
    echo "🛑 Stop containers: docker-compose down"
else
    echo "❌ Application failed to start. Check logs: docker-compose logs"
    exit 1
fi
