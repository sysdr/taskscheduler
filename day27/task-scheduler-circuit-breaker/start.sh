#!/bin/bash
set -e

echo "🚀 Starting Task Scheduler with Circuit Breaker..."

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    echo "❌ Maven wrapper not found. Please run the setup script first."
    exit 1
fi

# Start with Docker Compose
if command -v docker-compose &> /dev/null; then
    echo "🐳 Starting with Docker Compose..."
    docker-compose up -d
    
    echo "⏳ Waiting for services to start..."
    sleep 30
    
    echo "🎉 Services started successfully!"
    echo "🌐 Application: http://localhost:8080"
    echo "📊 Prometheus: http://localhost:9090"
    echo "📈 Grafana: http://localhost:3000 (admin/admin)"
    
else
    echo "🏃‍♂️ Starting with Java..."
    java -jar target/task-scheduler-circuit-breaker-1.0.0.jar &
    
    echo "⏳ Waiting for application to start..."
    sleep 20
    
    echo "🎉 Application started successfully!"
    echo "🌐 Application: http://localhost:8080"
fi

echo ""
echo "📋 Circuit Breaker Endpoints:"
echo "   Health: http://localhost:8080/actuator/health"
echo "   Metrics: http://localhost:8080/actuator/metrics"
echo "   Circuit Breakers: http://localhost:8080/actuator/circuitbreakers"

echo ""
echo "🔧 To simulate failures:"
echo "   curl -X POST http://localhost:8080/api/tasks/simulate-failure/payment?enable=true"
echo "   curl -X POST http://localhost:8080/api/tasks/simulate-failure/notification?enable=true"
