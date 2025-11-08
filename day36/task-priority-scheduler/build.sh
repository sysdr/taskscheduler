#!/bin/bash
set -e

echo "=== Building Task Priority Scheduler ==="

# Check for Maven
if ! command -v mvn &> /dev/null; then
    echo "Maven not found. Please install Maven first."
    exit 1
fi

# Clean and build
echo "Cleaning previous build..."
mvn clean

echo "Building application..."
mvn package -DskipTests

echo "✓ Build completed successfully!"
echo "JAR file: target/task-priority-scheduler-1.0.0.jar"
