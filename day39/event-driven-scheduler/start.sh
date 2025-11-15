#!/bin/bash

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=================================="
echo "Starting Event-Driven Scheduler"
echo "=================================="

# Start Docker services
echo "🐳 Starting Kafka and monitoring services..."
DOCKER_DIR="$SCRIPT_DIR/docker"
if [ -d "$DOCKER_DIR" ]; then
    cd "$DOCKER_DIR"
    docker compose up -d
    cd "$SCRIPT_DIR"
else
    echo "❌ Docker directory not found: $DOCKER_DIR"
    exit 1
fi

echo "⏳ Waiting for Kafka to be ready..."
sleep 10

# Start Spring Boot application
echo "🚀 Starting Spring Boot application..."
TARGET_JAR=$(find "$SCRIPT_DIR/target" -name "*.jar" -type f | head -1)
if [ -n "$TARGET_JAR" ]; then
    java -jar "$TARGET_JAR"
else
    echo "❌ JAR file not found in target directory. Please run ./build.sh first"
    exit 1
fi

echo ""
echo "Application started!"
echo "Dashboard: http://localhost:8080"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin)"
