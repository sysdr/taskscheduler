#!/bin/bash

echo "🧪 Running tests for Task Scheduler Message Queue..."

# Unit tests
echo "📋 Running unit tests..."
mvn test

# Integration test (requires running services)
echo "🔗 Running integration tests..."

# Check if services are running
if ! nc -z localhost 8080 2>/dev/null; then
    echo "❌ Spring Boot app is not running. Please start the services first with ./start.sh"
    exit 1
fi

if ! nc -z localhost 9092 2>/dev/null; then
    echo "❌ Kafka is not running. Please start the services first with ./start.sh"
    exit 1
fi

echo "✅ Services are running, proceeding with integration tests..."

# Test API endpoints
echo "🔍 Testing API endpoints..."

# Test health endpoint
HEALTH_RESPONSE=$(curl -s http://localhost:8080/actuator/health)
if [[ $HEALTH_RESPONSE == *"UP"* ]]; then
    echo "✅ Health check passed"
else
    echo "❌ Health check failed"
fi

# Test task stats endpoint
STATS_RESPONSE=$(curl -s http://localhost:8080/api/tasks/stats)
if [[ $STATS_RESPONSE == *"scheduled"* ]]; then
    echo "✅ Stats endpoint working"
else
    echo "❌ Stats endpoint failed"
fi

# Create sample tasks
echo "🎯 Creating sample tasks..."
SAMPLE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/demo/create-sample-tasks)
if [[ $SAMPLE_RESPONSE == *"sample tasks created"* ]]; then
    echo "✅ Sample tasks created successfully"
else
    echo "❌ Sample task creation failed"
fi

# Wait a bit and check if tasks were processed
echo "⏳ Waiting 30 seconds for task processing..."
sleep 30

# Check final stats
FINAL_STATS=$(curl -s http://localhost:8080/api/tasks/stats)
echo "📊 Final stats: $FINAL_STATS"

echo "🎉 Integration tests completed!"
