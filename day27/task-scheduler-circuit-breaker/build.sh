#!/bin/bash
set -e

echo "🏗️ Building Task Scheduler with Circuit Breaker..."

# Clean and build
./mvnw clean package -DskipTests

echo "✅ Build completed successfully!"
echo "📦 JAR file: target/task-scheduler-circuit-breaker-1.0.0.jar"
echo "🐳 Docker image ready to build"

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t task-scheduler-circuit-breaker:latest .

echo "🎉 Build process completed!"
echo "Run ./start.sh to start the application"
