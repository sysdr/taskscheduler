#!/bin/bash
echo "🎯 Running Timeout Task Scheduler Demo..."

BASE_URL="http://localhost:8080/api/tasks"

echo "📝 Submitting various tasks for timeout demonstration..."

# Submit fast task (should complete)
echo "1. Submitting fast task (1s timeout, 5s limit)..."
curl -X POST $BASE_URL/submit \
  -H "Content-Type: application/json" \
  -d '{"taskType":"FAST_TASK","timeoutSeconds":5,"payload":"Fast task demo"}'
echo ""

# Submit medium task (should complete)
echo "2. Submitting medium task (5s timeout, 10s limit)..."
curl -X POST $BASE_URL/submit \
  -H "Content-Type: application/json" \
  -d '{"taskType":"MEDIUM_TASK","timeoutSeconds":10,"payload":"Medium task demo"}'
echo ""

# Submit slow task with short timeout (should timeout)
echo "3. Submitting slow task with short timeout (15s task, 3s limit)..."
curl -X POST $BASE_URL/submit \
  -H "Content-Type: application/json" \
  -d '{"taskType":"SLOW_TASK","timeoutSeconds":3,"payload":"Timeout demo task"}'
echo ""

# Submit infinite task (should timeout)
echo "4. Submitting infinite task (should timeout after 5s)..."
curl -X POST $BASE_URL/submit \
  -H "Content-Type: application/json" \
  -d '{"taskType":"INFINITE_TASK","timeoutSeconds":5,"payload":"Infinite loop demo"}'
echo ""

# Submit random duration tasks
for i in {1..3}; do
    echo "$((4+i)). Submitting random duration task $i..."
    curl -X POST $BASE_URL/submit \
      -H "Content-Type: application/json" \
      -d "{\"taskType\":\"RANDOM_DURATION\",\"timeoutSeconds\":$((5+i*3)),\"payload\":\"Random task $i\"}"
    echo ""
done

echo "🎉 Demo tasks submitted! Check the dashboard at http://localhost:8080"
echo "📊 View all tasks: curl $BASE_URL"
