#!/bin/bash

echo "🎭 Running Distributed Lock Demo"
echo "==============================="

APP_URL="http://localhost:8080"

# Wait for application to be ready
echo "Waiting for application to be ready..."
until curl -s "$APP_URL/actuator/health" &> /dev/null; do
    echo "Waiting..."
    sleep 2
done

echo "✅ Application is ready"

# Test 1: Single task execution
echo ""
echo "📋 Test 1: Single Task Execution"
echo "--------------------------------"

TASK_KEY="demo-task-$(date +%s)"
echo "Executing task: $TASK_KEY"

curl -s -X POST "$APP_URL/api/tasks/execute" \
  -H "Content-Type: application/json" \
  -d "{\"taskKey\":\"$TASK_KEY\",\"taskType\":\"data-processing\"}" | jq .

# Test 2: Concurrent execution attempt
echo ""
echo "📋 Test 2: Concurrent Execution Test"
echo "-----------------------------------"

CONCURRENT_TASK="concurrent-demo-$(date +%s)"
echo "Attempting concurrent execution of: $CONCURRENT_TASK"

# Start multiple background requests
for i in {1..3}; do
    curl -s -X POST "$APP_URL/api/tasks/execute" \
      -H "Content-Type: application/json" \
      -d "{\"taskKey\":\"$CONCURRENT_TASK\",\"taskType\":\"report-generation\"}" \
      > "result_$i.json" &
done

wait

echo "Results:"
for i in {1..3}; do
    echo "Request $i:"
    cat "result_$i.json" | jq .status
    rm -f "result_$i.json"
done

# Test 3: Lock information
echo ""
echo "📋 Test 3: Lock Information"
echo "--------------------------"

echo "Lock statistics:"
curl -s "$APP_URL/api/locks/statistics" | jq .

echo ""
echo "✅ Demo completed"
echo ""
echo "🎯 Open your browser to $APP_URL to see the dashboard"
