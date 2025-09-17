#!/bin/bash
set -e

echo "🚀 Starting Idempotent Task Scheduler..."

# Check if using Docker
if [ "$1" = "docker" ]; then
    echo "🐳 Starting with Docker Compose..."
    docker-compose up --build -d
    echo "✅ Application started!"
    echo "🌐 Dashboard: http://localhost:8080"
    echo "📊 H2 Console: http://localhost:8080/h2-console"
    echo "🔍 View logs: docker-compose logs -f app"
else
    echo "☕ Starting with Java..."
    java -jar target/*.jar
fi
