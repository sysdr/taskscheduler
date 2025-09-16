#!/bin/bash

echo "🚀 Starting Spring Cloud Coordination Application..."

# Check if Redis is running
if ! redis-cli ping >/dev/null 2>&1; then
    echo "⚠️  Redis not detected. Starting Redis with Docker..."
    docker run -d --name redis-coordination -p 6379:6379 redis:7-alpine
    sleep 3
fi

# Start application
echo "🎯 Starting application on http://localhost:8080"
echo "📊 Dashboard: http://localhost:8080/ui/"
echo "🔍 H2 Console: http://localhost:8080/h2-console"
echo "📈 Actuator: http://localhost:8080/actuator"
echo ""
echo "Press Ctrl+C to stop"

java -jar target/spring-cloud-coordination-*.jar
