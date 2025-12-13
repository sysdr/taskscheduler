#!/bin/bash
set -e

echo "Building Task Scheduler..."

# Build with Maven
if [ -f "mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi

# Build Docker image
echo "Building Docker image..."
docker build -t task-scheduler:1.0.0 .

echo "Build complete!"
echo "Docker image: task-scheduler:1.0.0"
