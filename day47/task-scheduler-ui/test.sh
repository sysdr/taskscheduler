#!/bin/bash
echo "Running functional tests..."

PORT="${PORT:-8081}"
BASE_URL="http://localhost:${PORT}"

# Wait for application to start
echo "Waiting for application to start..."
for i in {1..30}; do
    if curl -s "$BASE_URL/api/tasks" > /dev/null 2>&1; then
        echo "Application is ready!"
        break
    fi
    sleep 1
done

# Test 1: Get all tasks
echo "Test 1: Fetching all tasks..."
curl -s "$BASE_URL/api/tasks" | grep -q "name" && echo "✓ Pass" || echo "✗ Fail"

# Test 2: Get metrics
echo "Test 2: Fetching metrics..."
curl -s "$BASE_URL/api/tasks/metrics" | grep -q "totalTasks" && echo "✓ Pass" || echo "✗ Fail"

# Test 3: Create new task
echo "Test 3: Creating new task..."
RESPONSE=$(curl -s -X POST "$BASE_URL/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Task","description":"Test","type":"ONE_TIME"}')
echo $RESPONSE | grep -q "id" && echo "✓ Pass" || echo "✗ Fail"

# Test 4: Access dashboard
echo "Test 4: Accessing dashboard..."
curl -s "$BASE_URL/" | grep -q "Task Scheduler" && echo "✓ Pass" || echo "✗ Fail"

echo ""
echo "All tests completed!"
echo "Open browser to ${BASE_URL} to view dashboard"
