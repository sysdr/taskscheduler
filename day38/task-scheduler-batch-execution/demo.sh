#!/bin/bash

echo "Day 38: Batching Task Executions - Demo"
echo "======================================="
echo ""

# Check if app is running
if ! curl -s http://localhost:8038/actuator/health > /dev/null 2>&1; then
    echo "Error: Application not running. Start with ./start.sh"
    exit 1
fi

echo "1. Creating initial batch of 100 tasks..."
curl -s -X POST http://localhost:8038/api/tasks/batch \
    -H "Content-Type: application/json" \
    -d '{"count": 100, "taskTypes": ["EMAIL", "SMS", "PUSH"]}' | jq .

sleep 3

echo ""
echo "2. Current statistics:"
curl -s http://localhost:8038/api/tasks/stats | jq .

echo ""
echo "3. Creating large batch of 1000 tasks..."
curl -s -X POST http://localhost:8038/api/tasks/batch \
    -H "Content-Type: application/json" \
    -d '{"count": 1000, "taskTypes": ["EMAIL", "SMS", "PUSH", "REPORT"]}' | jq .

echo ""
echo "4. Waiting for processing..."
sleep 5

echo ""
echo "5. Final statistics:"
curl -s http://localhost:8038/api/tasks/stats | jq .

echo ""
echo "6. Recent batch metrics (last 5):"
curl -s http://localhost:8038/api/tasks/batches | jq '.[0:5]'

echo ""
echo "=================================="
echo "Demo completed!"
echo "View dashboard: http://localhost:8038"
echo "=================================="
