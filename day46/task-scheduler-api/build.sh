#!/bin/bash

echo "Building Task Scheduler API..."

# Install dependencies and build
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Build Docker image
    echo "Building Docker image..."
    docker build -t task-scheduler-api:latest .
    
    echo "✅ Docker image built successfully!"
    echo ""
    echo "Run './start.sh' to start the application"
else
    echo "❌ Build failed!"
    exit 1
fi
