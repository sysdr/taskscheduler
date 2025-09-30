#!/bin/bash

echo "🚀 Starting Task Scheduler Day 24..."

# Check if JAR exists
if [ ! -f "target/task-scheduler-day24-1.0.0.jar" ]; then
    echo "❌ JAR file not found. Please run build.sh first."
    exit 1
fi

# Start application
echo "▶️ Starting application..."
java -jar target/task-scheduler-day24-1.0.0.jar

echo "✅ Application started!"
echo "🌐 Dashboard: http://localhost:8080"
echo "🔧 H2 Console: http://localhost:8080/h2-console"
