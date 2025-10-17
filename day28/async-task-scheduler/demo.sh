#!/bin/bash
set -e

echo "🎬 Running Async Task Scheduler Demo..."

BASE_URL="http://localhost:8080"

# Wait for application to start
echo "Waiting for application to start..."
sleep 10

# Check if app is running
if ! curl -s "$BASE_URL/actuator/health" > /dev/null; then
    echo "❌ Application not running. Start with './start.sh' first"
    exit 1
fi

echo "✅ Application is running!"
echo ""

# Create sample tasks
echo "📧 Creating email tasks..."
curl -s -X POST "$BASE_URL/api/tasks/sample/email" > /dev/null
curl -s -X POST "$BASE_URL/api/tasks/sample/email" > /dev/null

echo "📊 Creating report tasks..."
curl -s -X POST "$BASE_URL/api/tasks/sample/report" > /dev/null
curl -s -X POST "$BASE_URL/api/tasks/sample/report" > /dev/null

echo "💾 Creating data tasks..."
curl -s -X POST "$BASE_URL/api/tasks/sample/data" > /dev/null
curl -s -X POST "$BASE_URL/api/tasks/sample/data" > /dev/null

echo ""
echo "🎉 Demo tasks created!"
echo "Open http://localhost:8080 to see async execution in real-time"
echo "Watch as multiple tasks execute concurrently in different thread pools"
