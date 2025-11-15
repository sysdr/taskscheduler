#!/bin/bash

echo "=================================="
echo "Testing Event-Driven Scheduler"
echo "=================================="

echo "🧪 Running unit tests..."
mvn test

echo ""
echo "🔍 Running integration tests..."

# Wait for application to be ready
sleep 5

# Test file upload event
echo "Testing file upload event..."
curl -X POST http://localhost:8080/api/events/demo/file-upload

sleep 2

# Test user registration event
echo "Testing user registration event..."
curl -X POST http://localhost:8080/api/events/demo/user-registration

sleep 2

# Test system alert event
echo "Testing system alert event..."
curl -X POST http://localhost:8080/api/events/demo/system-alert

sleep 3

# Check metrics
echo ""
echo "📊 Current Metrics:"
curl -s http://localhost:8080/api/tasks/metrics | python3 -m json.tool

echo ""
echo "✅ Tests completed! Check dashboard at http://localhost:8080"
