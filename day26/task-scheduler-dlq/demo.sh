#!/bin/bash
set -e

echo "🎯 Running Dead Letter Queue Demo..."

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

echo "📝 Creating demo tasks..."
curl -X POST http://localhost:8080/api/tasks/demo/create-test-tasks

echo ""
echo "🔍 Current task status:"
curl -s http://localhost:8080/api/dashboard/stats | jq '.'

echo ""
echo "⏰ Waiting 30 seconds for task processing and failures..."
sleep 30

echo ""
echo "💀 Dead Letter Queue status:"
curl -s http://localhost:8080/api/dlq/stats | jq '.'

echo ""
echo "📊 Visit the dashboard at http://localhost:8080 to see the results!"
echo "💀 Visit the DLQ dashboard at http://localhost:8080/dlq to manage failed tasks!"
