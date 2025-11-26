#!/bin/bash

echo "Starting Task Scheduler API..."

# Check if running with Docker
if [ "$1" == "--docker" ]; then
    echo "Starting with Docker Compose..."
    docker-compose up -d
    echo "✅ Application started with Docker!"
else
    echo "Starting with Maven..."
    mvn spring-boot:run &
    echo $! > app.pid
    echo "✅ Application started!"
fi

echo ""
echo "===========================================  "
echo "🚀 Task Scheduler API is running!"
echo "==========================================="
echo "📊 Dashboard: http://localhost:8080"
echo "🔧 API Base: http://localhost:8080/api/v1"
echo "💾 H2 Console: http://localhost:8080/h2-console"
echo "📈 Actuator: http://localhost:8080/actuator"
echo "==========================================="
