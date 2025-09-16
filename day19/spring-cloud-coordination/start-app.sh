#!/bin/bash
echo "Starting Spring Cloud Coordination Application..."
echo "Current directory: $(pwd)"
echo "Java version: $(java -version 2>&1 | head -1)"
echo "Maven version: $(mvn -version 2>&1 | head -1)"

# Check if Redis is running
if ! redis-cli ping >/dev/null 2>&1; then
    echo "Redis is not running. Starting Redis..."
    docker run -d --name redis-coordination -p 6379:6379 redis:7-alpine
    sleep 5
fi

echo "Starting application with Maven..."
mvn spring-boot:run
