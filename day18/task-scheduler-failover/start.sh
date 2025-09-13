#!/bin/bash
set -e

if [ "$1" == "docker" ]; then
    echo "🐳 Starting with Docker Compose..."
    cd docker
    docker-compose up --build -d
    echo ""
    echo "🌐 Access the dashboards:"
    echo "   Node 1: http://localhost:8081"
    echo "   Node 2: http://localhost:8082" 
    echo "   Node 3: http://localhost:8083"
    echo ""
    echo "📊 API endpoints:"
    echo "   Status: http://localhost:8081/api/scheduler/status"
    echo "   Health: http://localhost:8081/api/scheduler/health"
    echo ""
    echo "🛑 To stop: ./stop.sh docker"
else
    echo "🚀 Starting Task Scheduler..."
    
    # Copy web resources to classpath
    mkdir -p target/classes/static target/classes/templates
    cp -r web/static/* target/classes/static/ 2>/dev/null || true
    cp web/templates/* target/classes/templates/ 2>/dev/null || true
    
    java -jar target/task-scheduler-failover-1.0.0.jar &
    
    echo "✅ Application started!"
    echo ""
    echo "🌐 Dashboard: http://localhost:8080"
    echo "📊 API Status: http://localhost:8080/api/scheduler/status"
    echo "💾 H2 Console: http://localhost:8080/h2-console"
    echo ""
    echo "🛑 To stop: ./stop.sh"
fi
