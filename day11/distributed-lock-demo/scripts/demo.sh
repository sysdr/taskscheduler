#!/bin/bash

echo "🎬 Distributed Lock Problem Demo"
echo "================================"

# Wait for instances to start
echo "⏳ Waiting for instances to start..."
sleep 10

echo "🔍 Checking for race conditions..."

# Check dashboard
echo "📊 Dashboard Status:"
curl -s http://localhost:8080/api/monitoring/dashboard | jq '.'

echo ""
echo "🔄 Recent Critical Task Executions:"
curl -s http://localhost:8080/api/monitoring/recent/CriticalBusinessTask | jq '.'

echo ""
echo "💰 Recent Financial Task Executions:"  
curl -s http://localhost:8080/api/monitoring/recent/DailyFinancialCalculation | jq '.'

echo ""
echo "⚠️  The race condition problem:"
echo "   - Multiple instances are executing the same scheduled tasks"
echo "   - This leads to duplicate processing and potential data corruption"
echo "   - Tomorrow we'll fix this with distributed locks!"
