#!/bin/bash
set -e

echo "Building Task Scheduler with Docker..."

# Build with Maven
mvn clean package -DskipTests

# Build Docker image
docker build -t task-scheduler:1.0.0 .

echo "✅ Build complete! Image: task-scheduler:1.0.0"
