#!/bin/bash

echo "🔨 Building Spring Retry Task Scheduler..."

# Clean and compile
mvn clean compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package application
echo "📦 Packaging application..."
mvn package -DskipTests

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t spring-retry-task-scheduler:latest .

echo "✅ Build completed successfully!"
