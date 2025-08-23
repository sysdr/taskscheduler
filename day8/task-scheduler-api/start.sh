#!/bin/bash

set -e

echo "🚀 Starting Task Scheduler API - Day 8"
echo "======================================"

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Java installation
if ! command_exists java; then
    echo "❌ Java is not installed. Please install Java 21 or later."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java 21 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check Maven installation
if ! command_exists mvn; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or later."
    exit 1
fi

echo "✅ Java and Maven are installed"

# Build application
echo "🔨 Building application..."
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi

# Run tests
echo "🧪 Running tests..."
./mvnw test

if [ $? -eq 0 ]; then
    echo "✅ All tests passed"
else
    echo "⚠️  Some tests failed, but continuing..."
fi

# Check if Docker is available and user wants to use it
if command_exists docker && command_exists docker-compose; then
    echo ""
    echo "🐳 Docker is available. Choose deployment method:"
    echo "1. Run with H2 (in-memory database)"
    echo "2. Run with Docker Compose (PostgreSQL)"
    echo ""
    read -p "Enter your choice (1 or 2): " choice
    
    case $choice in
        2)
            echo "🐳 Starting with Docker Compose..."
            cd docker
            docker-compose up --build -d
            echo ""
            echo "✅ Services started with Docker Compose"
            echo "📊 API: http://localhost:8080/api/tasks"
            echo "🗄️  Database: PostgreSQL on localhost:5432"
            echo ""
            echo "To stop: cd docker && docker-compose down"
            exit 0
            ;;
        1|*)
            echo "🏃 Running with H2 database..."
            ;;
    esac
fi

# Start application with H2
echo "🏃 Starting Task Scheduler API..."
echo "Using H2 in-memory database"

java -jar target/*.jar &
APP_PID=$!

echo "Application PID: $APP_PID"

# Wait for application to start
echo "⏳ Waiting for application to start..."
for i in {1..30}; do
    if curl -f http://localhost:8080/api/tasks/health >/dev/null 2>&1; then
        echo "✅ Application started successfully!"
        break
    fi
    echo "Waiting... ($i/30)"
    sleep 2
done

if ! curl -f http://localhost:8080/api/tasks/health >/dev/null 2>&1; then
    echo "❌ Application failed to start properly"
    kill $APP_PID 2>/dev/null || true
    exit 1
fi

echo ""
echo "🎉 Task Scheduler API is running!"
echo "================================="
echo "📊 API Base URL: http://localhost:8080/api"
echo "🏥 Health Check: http://localhost:8080/api/tasks/health"
echo "📋 List Tasks: http://localhost:8080/api/tasks"
echo "🗄️  H2 Console: http://localhost:8080/api/h2-console"
echo "   - JDBC URL: jdbc:h2:mem:taskscheduler"
echo "   - Username: sa"
echo "   - Password: password"
echo "📈 Actuator: http://localhost:8080/api/actuator/health"
echo ""
echo "🧪 Demo API calls:"
echo ""

# Demo API calls
echo "1. Creating a sample task..."
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Report Generation",
    "description": "Generate daily reports for management",
    "cronExpression": "0 0 9 * * MON-FRI",
    "status": "ACTIVE",
    "taskClass": "com.example.ReportTask",
    "parameters": "{\"format\":\"PDF\",\"recipients\":[\"manager@company.com\"]}"
  }' | jq '.' 2>/dev/null || echo "Sample task created"

echo ""
echo "2. Listing all tasks..."
curl -s http://localhost:8080/api/tasks | jq '.[0] // "No tasks found"' 2>/dev/null || echo "Tasks retrieved"

echo ""
echo "3. Getting task counts..."
curl -s http://localhost:8080/api/tasks/count | jq '.' 2>/dev/null || echo "Counts retrieved"

echo ""
echo "🛑 To stop the application, run: ./stop.sh"

# Save PID for stop script
echo $APP_PID > .app.pid
wait $APP_PID
