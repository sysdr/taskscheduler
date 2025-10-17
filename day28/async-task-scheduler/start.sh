#!/bin/bash
set -e

echo "🚀 Starting Async Task Scheduler..."

# Build if needed
if [ ! -f "build/libs/async-task-scheduler-1.0.0.jar" ]; then
    echo "📦 Building application first..."
    ./build.sh
fi

# Start the application
echo "🔥 Starting Spring Boot application..."
echo "Dashboard will be available at: http://localhost:8080"
echo "API documentation at: http://localhost:8080/swagger-ui.html"
echo "H2 Console at: http://localhost:8080/h2-console"
echo ""
echo "Press Ctrl+C to stop the application"

java -jar build/libs/async-task-scheduler-1.0.0.jar
