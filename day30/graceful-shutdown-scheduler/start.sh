#!/bin/bash

set -e

echo "🚀 Starting Graceful Shutdown Task Scheduler..."

# Check if jar file exists
if [ ! -f "target/graceful-shutdown-scheduler-1.0.0.jar" ]; then
    echo "❌ JAR file not found. Please run build.sh first."
    exit 1
fi

echo "🌟 Starting application on port 8080..."
echo "📊 Dashboard will be available at: http://localhost:8080"
echo "🔧 H2 Console available at: http://localhost:8080/h2-console"
echo "📈 Actuator endpoints: http://localhost:8080/actuator"
echo ""
echo "Press Ctrl+C to stop the application"
echo "=================================================="

java -jar target/graceful-shutdown-scheduler-1.0.0.jar
