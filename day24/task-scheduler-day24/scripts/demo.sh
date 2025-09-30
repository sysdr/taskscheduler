#!/bin/bash

echo "🎭 Running Task Scheduler Day 24 Demo..."

BASE_URL="http://localhost:8080"

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 5

# Check if application is running
if ! curl -s "$BASE_URL/actuator/health" > /dev/null; then
    echo "❌ Application is not running. Please start it first."
    exit 1
fi

echo "✅ Application is running!"

# Create sample tasks
echo "📝 Creating sample tasks..."

# Email tasks (some will fail)
curl -X POST "$BASE_URL/tasks" \
    -d "name=Send Welcome Email" \
    -d "taskType=email" \
    -d "description=Send welcome email to new user" \
    -d "priority=HIGH" \
    -d "taskData={\"email\":\"user@example.com\"}" \
    -d "maxRetries=3"

curl -X POST "$BASE_URL/tasks" \
    -d "name=Send Invalid Email" \
    -d "taskType=email" \
    -d "description=This will fail permanently" \
    -d "priority=NORMAL" \
    -d "taskData={\"email\":\"invalid@example.com\"}" \
    -d "maxRetries=2"

# Payment tasks
curl -X POST "$BASE_URL/tasks" \
    -d "name=Process Payment" \
    -d "taskType=payment" \
    -d "description=Process customer payment" \
    -d "priority=CRITICAL" \
    -d "taskData={\"amount\":100.00,\"currency\":\"USD\"}" \
    -d "maxRetries=5"

curl -X POST "$BASE_URL/tasks" \
    -d "name=Insufficient Funds Payment" \
    -d "taskType=payment" \
    -d "description=This will fail due to insufficient funds" \
    -d "priority=HIGH" \
    -d "taskData={\"amount\":1000.00,\"currency\":\"USD\",\"account\":\"insufficient\"}" \
    -d "maxRetries=3"

# Report generation tasks
curl -X POST "$BASE_URL/tasks" \
    -d "name=Generate Monthly Report" \
    -d "taskType=report" \
    -d "description=Generate monthly sales report" \
    -d "priority=NORMAL" \
    -d "taskData={\"month\":\"December\",\"year\":2024}" \
    -d "maxRetries=2"

# Cleanup tasks
curl -X POST "$BASE_URL/tasks" \
    -d "name=Cleanup Temp Files" \
    -d "taskType=cleanup" \
    -d "description=Clean up temporary files" \
    -d "priority=LOW" \
    -d "taskData={\"directory\":\"/tmp\"}" \
    -d "maxRetries=4"

# Generic tasks
for i in {1..5}; do
    curl -X POST "$BASE_URL/tasks" \
        -d "name=Generic Task $i" \
        -d "taskType=generic" \
        -d "description=Generic task for demonstration" \
        -d "priority=NORMAL" \
        -d "taskData={\"iteration\":$i}" \
        -d "maxRetries=3"
done

echo "✅ Demo tasks created!"
echo ""
echo "🎯 Demo Objectives:"
echo "1. Watch tasks execute automatically every 5 seconds"
echo "2. Observe retry behavior with exponential backoff"
echo "3. See how different task types handle failures"
echo "4. Monitor retry statistics in the dashboard"
echo ""
echo "🌐 Open your browser to: $BASE_URL"
echo "📊 Monitor the dashboard to see:"
echo "   - Tasks being processed"
echo "   - Automatic retries with delays"
echo "   - Exponential backoff in action"
echo "   - Success vs permanent failure rates"
echo ""
echo "⏰ Tasks will process automatically. Watch the status changes!"
