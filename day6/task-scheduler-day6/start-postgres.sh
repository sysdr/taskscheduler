#!/bin/bash

echo "🚀 Starting Task Scheduler Day 6 with PostgreSQL..."

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for Docker
if ! command_exists docker; then
    echo "❌ Docker not found. Please install Docker."
    exit 1
fi

if ! command_exists docker-compose; then
    echo "❌ Docker Compose not found. Please install Docker Compose."
    exit 1
fi

# Start PostgreSQL with Docker Compose
echo "🐘 Starting PostgreSQL database..."
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 10

# Check if PostgreSQL is running
if ! docker ps | grep -q task-scheduler-postgres; then
    echo "❌ Failed to start PostgreSQL"
    exit 1
fi

echo "✅ PostgreSQL is running"

# Clean and compile
echo "🧹 Cleaning and compiling..."
mvn clean compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package application
echo "📦 Packaging application..."
mvn package -DskipTests

# Start with PostgreSQL (production mode)
echo "🎯 Starting application with PostgreSQL..."
echo "📊 Dashboard: http://localhost:8080"
echo "🗄️ Adminer (DB Admin): http://localhost:8081"
echo "💡 Health Check: http://localhost:8080/actuator/health"
echo ""
echo "Database Connection:"
echo "  Host: localhost:5432"
echo "  Database: taskscheduler"
echo "  Username: taskuser"
echo "  Password: taskpass"
echo ""
echo "To stop the application, press Ctrl+C"
echo ""

java -jar target/task-scheduler-day6-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

