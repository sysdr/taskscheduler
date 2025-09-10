#!/bin/bash

set -e

echo "=== Starting Task Scheduler Leader Election Services ==="

# Start MySQL if not running
cd docker
docker-compose up -d mysql

# Wait for MySQL
echo "Waiting for MySQL to start..."
sleep 10

# Go back to project root
cd ..

# Start first instance in background
echo "Starting Application Instance 1 on port 8080..."
SERVER_PORT=8080 ./mvnw spring-boot:run &
APP1_PID=$!

# Wait a bit before starting second instance
sleep 5

# Start second instance in background
echo "Starting Application Instance 2 on port 8081..."
SERVER_PORT=8081 ./mvnw spring-boot:run &
APP2_PID=$!

# Save PIDs for cleanup
echo $APP1_PID > .app1.pid
echo $APP2_PID > .app2.pid

echo "✅ Both instances started!"
echo "Instance 1: http://localhost:8080/api/status"
echo "Instance 2: http://localhost:8081/api/status"
echo "API Documentation: http://localhost:8080/swagger-ui.html"
echo ""
echo "Monitor leadership by checking status endpoints:"
echo "curl http://localhost:8080/api/status"
echo "curl http://localhost:8081/api/status"
echo ""
echo "Use './stop.sh' to stop all services"

wait
