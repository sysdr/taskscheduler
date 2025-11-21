#!/bin/bash
set -e

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Starting Task Scheduler Monitoring Stack..."
echo "Working directory: $SCRIPT_DIR"

# Check if JAR file exists
JAR_FILE="$SCRIPT_DIR/target/task-scheduler-monitoring-1.0.0.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Please run ./build.sh first"
    exit 1
fi

# Start Docker containers
echo "Starting Prometheus and Grafana..."
docker-compose up -d

# Wait for containers to be healthy
echo "Waiting for monitoring stack to be ready..."
sleep 10

# Start Spring Boot application
echo "Starting Task Scheduler application..."
java -jar "$JAR_FILE" &
APP_PID=$!
echo "Application started with PID: $APP_PID"
echo $APP_PID > app.pid

echo ""
echo "=========================================="
echo "Task Scheduler Monitoring is running!"
echo "=========================================="
echo "Application Dashboard: http://localhost:8080"
echo "Prometheus Metrics: http://localhost:8080/actuator/prometheus"
echo "Prometheus UI: http://localhost:9090"
echo "Grafana Dashboard: http://localhost:3000 (admin/admin)"
echo ""
echo "To stop: ./stop.sh"
