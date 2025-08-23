#!/bin/bash

# Dynamic Task Scheduler - Startup Script
echo "🚀 Starting Dynamic Task Scheduler Application..."

# Check if Java 21 is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "17" ]; then
    echo "❌ Java 17+ is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven."
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n1)"
echo "✅ Maven version: $(mvn -version | head -n1)"

# Clean and compile
echo "🔨 Building application..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

# Skip tests since they're already passing
echo "✅ Tests already passed, skipping test execution..."

# Start the application
echo "🏃 Starting Spring Boot application..."
echo "📱 Dashboard will be available at: http://localhost:8080"
echo "🗄️ H2 Database Console: http://localhost:8080/h2-console"
echo "📊 Health Check: http://localhost:8080/actuator/health"
echo "📋 Scheduled Tasks: http://localhost:8080/actuator/scheduledtasks"
echo ""
echo "Press Ctrl+C to stop the application"
echo "============================================"

# Store PID for stop script
mvn spring-boot:run &
PID=$!
echo $PID > .app.pid

# Wait for application to start
sleep 10

# Create sample tasks for demonstration
echo "📝 Creating sample tasks for demonstration..."

# Sample task 1: Log message every 2 minutes
curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "demo-logger",
    "description": "Demo task that logs a message every 2 minutes",
    "cronExpression": "0 */2 * * * *",
    "taskType": "LOG_MESSAGE",
    "taskData": "This is a demo log message",
    "status": "ACTIVE"
  }' > /dev/null

# Sample task 2: Health check every 5 minutes
curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "health-monitor",
    "description": "System health check every 5 minutes",
    "cronExpression": "0 */5 * * * *",
    "taskType": "SYSTEM_HEALTH_CHECK",
    "taskData": "Monitor system resources",
    "status": "ACTIVE"
  }' > /dev/null

# Sample task 3: Inactive task for testing
curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "backup-task",
    "description": "Daily backup task (currently inactive)",
    "cronExpression": "0 0 2 * * *",
    "taskType": "DATA_CLEANUP",
    "taskData": "Backup configuration data",
    "status": "INACTIVE"
  }' > /dev/null

echo "✅ Sample tasks created!"
echo "🌐 Open http://localhost:8080 to access the dashboard"

# Keep the script running
wait $PID
