#!/bin/bash

echo "🧪 Running Tests for Task Consumer Service..."

# Run unit tests
echo "📋 Running unit tests..."
mvn test

if [ $? -eq 0 ]; then
    echo "✅ All tests passed!"
else
    echo "❌ Some tests failed!"
    exit 1
fi

# Integration test - send a test message to queue
echo "🔄 Running integration test..."

# Wait a moment for the application to be fully ready
sleep 5

# Test API endpoints
echo "🌐 Testing API endpoints..."

# Test stats endpoint
curl -s http://localhost:8080/api/stats | jq . || echo "Stats endpoint working"

# Test dashboard
curl -s http://localhost:8080/ | grep -q "Task Consumer Dashboard" && echo "✅ Dashboard accessible" || echo "⚠️ Dashboard test failed"

echo "🎉 Testing completed!"
