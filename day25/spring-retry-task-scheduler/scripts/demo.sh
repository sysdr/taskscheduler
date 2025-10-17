#!/bin/bash

BASE_URL="http://localhost:8080"

echo "🎯 Spring Retry Task Scheduler Demo"
echo "=================================="

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

# Create sample tasks
echo "📝 Creating sample tasks..."

# Email task (likely to retry)
curl -X POST "$BASE_URL/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Welcome Email",
    "description": "Send welcome email to new user",
    "type": "EMAIL_NOTIFICATION",
    "maxRetries": 3
  }'

echo ""

# Database cleanup task
curl -X POST "$BASE_URL/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Cleanup",
    "description": "Clean up old database records",
    "type": "DATABASE_CLEANUP",
    "maxRetries": 5
  }'

echo ""

# API sync task
curl -X POST "$BASE_URL/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Customer Data Sync",
    "description": "Sync customer data with external API",
    "type": "API_SYNC",
    "maxRetries": 4
  }'

echo ""

echo "✅ Sample tasks created!"

# Execute tasks
echo "🚀 Executing tasks..."

for i in {1..3}; do
  echo "Executing task $i..."
  curl -X POST "$BASE_URL/api/tasks/$i/execute"
  echo ""
  sleep 2
done

echo ""
echo "📊 Check task status:"
echo "All tasks: curl $BASE_URL/api/tasks"
echo "Failed tasks: curl $BASE_URL/api/tasks/status/FAILED"
echo "Retryable tasks: curl $BASE_URL/api/tasks/retryable"
echo "Dead letter tasks: curl $BASE_URL/api/tasks/dead-letter"
echo ""
echo "📈 Metrics: $BASE_URL/actuator/metrics"
echo "🏥 Health: $BASE_URL/actuator/health"
