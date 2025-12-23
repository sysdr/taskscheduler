#!/bin/bash

echo "🚀 Starting Workflow Comparison System..."

# Check if option provided as argument, otherwise default to 1
OPTION=${1:-1}

if [ "$OPTION" = "2" ]; then
    echo ""
    echo "Starting Temporal server with Docker Compose..."
    if [ -d "docker" ]; then
        cd docker
        docker-compose up -d
        cd ..
        
        echo "Waiting for Temporal server to be ready..."
        sleep 10
        
        echo ""
        echo "Temporal UI available at: http://localhost:8080"
    else
        echo "❌ Error: docker directory not found"
        exit 1
    fi
else
    echo "Running with H2 (in-memory) - No Temporal server"
fi

# Check if JAR exists
if [ ! -f "target/workflow-comparison-1.0.0.jar" ]; then
    echo "❌ Error: JAR file not found. Please run './build.sh' first."
    exit 1
fi

echo ""
echo "Starting Spring Boot application..."
java -jar target/workflow-comparison-1.0.0.jar

echo ""
echo "Application started!"
echo "Dashboard: http://localhost:8080"
if [ "$OPTION" = "2" ]; then
    echo "Temporal UI: http://localhost:8080"
fi
