#!/bin/bash
set -e

echo "🚀 Starting Cron Validator Service..."

# Check if jar exists
if [ ! -f "target/cron-validator-service-1.0.0.jar" ]; then
    echo "📦 JAR not found. Building..."
    ./build.sh
fi

# Start the application
echo "🎯 Starting application on port 8080..."
java -jar target/cron-validator-service-1.0.0.jar &

APP_PID=$!
echo $APP_PID > app.pid

echo "✅ Application started with PID: $APP_PID"
echo "🌐 Access the dashboard at: http://localhost:8080"
echo "📊 Health check: http://localhost:8080/actuator/health"
echo ""
echo "To stop: ./stop.sh"
