#!/bin/bash
set -e

echo "🚀 Starting Task Scheduler with Dead Letter Queue..."

# Build if JAR doesn't exist
if [ ! -f "target/task-scheduler-dlq-1.0.0-SNAPSHOT.jar" ]; then
    echo "📦 Building application first..."
    ./build.sh
fi

# Start the application
echo "🔥 Starting Spring Boot application..."
java -jar target/task-scheduler-dlq-1.0.0-SNAPSHOT.jar &

# Store PID
echo $! > app.pid

echo "✅ Application started!"
echo "🌐 Dashboard: http://localhost:8080"
echo "💀 DLQ Monitor: http://localhost:8080/dlq"
echo "📊 Metrics: http://localhost:8080/actuator/metrics"
echo "🗄️  Database Console: http://localhost:8080/h2-console"
echo ""
echo "📝 To stop the application, run: ./stop.sh"
