#!/bin/bash
echo "Testing Timezone-Aware Scheduler..."

# Wait for application to start
sleep 10

BASE_URL="http://localhost:8080"

echo "1. Testing health endpoint..."
curl -s $BASE_URL/actuator/health | grep -q "UP" && echo "✓ Health check passed" || echo "✗ Health check failed"

echo "2. Getting time zones..."
ZONES=$(curl -s $BASE_URL/api/tasks/timezones | grep -c "America")
echo "✓ Found $ZONES time zones"

echo "3. Creating test task..."
TASK_ID=$(curl -s -X POST $BASE_URL/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Task",
    "description": "Daily test execution",
    "timeZone": "America/New_York",
    "scheduledTime": "14:30"
  }' | grep -o '"id":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TASK_ID" ]; then
    echo "✓ Task created with ID: $TASK_ID"
else
    echo "✗ Failed to create task"
fi

echo "4. Fetching all tasks..."
curl -s $BASE_URL/api/tasks | grep -q "Test Task" && echo "✓ Task retrieved" || echo "✗ Task not found"

echo "5. Getting time zone info..."
curl -s $BASE_URL/api/tasks/timezones/America_New_York/info | grep -q "America/New_York" && echo "✓ Time zone info retrieved" || echo "✗ Failed"

echo ""
echo "All tests completed! Access dashboard at http://localhost:8080"
