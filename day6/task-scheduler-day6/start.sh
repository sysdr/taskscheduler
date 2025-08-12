#!/bin/bash

echo "🚀 Starting Task Scheduler Day 6..."

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for required tools
if ! command_exists java; then
    echo "❌ Java not found. Please install Java 21 or later."
    exit 1
fi

if ! command_exists mvn; then
    echo "❌ Maven not found. Please install Maven."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java 21 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $JAVA_VERSION"

# Clean and compile
echo "🧹 Cleaning and compiling..."
mvn clean compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package application
echo "📦 Packaging application..."
mvn package -DskipTests

# Start with H2 database (development mode)
echo "🎯 Starting application with H2 database..."
echo "📊 Dashboard: http://localhost:8080"
echo "🗄️ H2 Console: http://localhost:8080/h2-console"
echo "💡 Health Check: http://localhost:8080/actuator/health"
echo ""
echo "To stop the application, press Ctrl+C"
echo ""

java -jar target/task-scheduler-day6-0.0.1-SNAPSHOT.jar

