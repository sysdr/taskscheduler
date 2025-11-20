#!/bin/bash

echo "========================================="
echo "Testing Task Scheduler Metrics"
echo "========================================="

BASE_URL="http://localhost:8080"

# Check health
echo ""
echo "1. Health Check..."
curl -s "$BASE_URL/actuator/health" | head -c 200
echo ""

# Submit tasks
echo ""
echo "2. Submitting test tasks..."

curl -s -X POST "$BASE_URL/api/tasks" \
    -H "Content-Type: application/json" \
    -d '{"name":"Email-Test","type":"email","priority":"HIGH"}' | head -c 100
echo ""

curl -s -X POST "$BASE_URL/api/tasks" \
    -H "Content-Type: application/json" \
    -d '{"name":"Report-Test","type":"report","priority":"MEDIUM"}' | head -c 100
echo ""

# Generate batch
echo ""
echo "3. Generating batch of 20 tasks..."
curl -s -X POST "$BASE_URL/api/tasks/generate/20"
echo ""

# Wait for processing
echo ""
echo "4. Waiting 5 seconds for task processing..."
sleep 5

# Check metrics
echo ""
echo "5. Metrics Summary:"
curl -s "$BASE_URL/api/metrics/summary" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/metrics/summary"
echo ""

# Check timers
echo ""
echo "6. Timer Metrics:"
curl -s "$BASE_URL/api/metrics/timers" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/metrics/timers"
echo ""

# Check Actuator metrics
echo ""
echo "7. Actuator Task Metrics:"
curl -s "$BASE_URL/actuator/metrics/task.submitted.total"
echo ""
curl -s "$BASE_URL/actuator/metrics/task.completed.total"
echo ""

# Check Prometheus endpoint
echo ""
echo "8. Prometheus Metrics (sample):"
curl -s "$BASE_URL/actuator/prometheus" | grep "task_" | head -20
echo ""

# Task stats
echo ""
echo "9. Task Statistics:"
curl -s "$BASE_URL/api/tasks/stats" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/tasks/stats"
echo ""

echo ""
echo "========================================="
echo "Testing Complete!"
echo "========================================="
echo ""
echo "Open http://localhost:8080 for the dashboard"
