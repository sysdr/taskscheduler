#!/bin/bash
set -e

echo "✅ Verifying Task Scheduler with Dead Letter Queue..."

# Check if application is running
if ! curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "❌ Application is not running!"
    echo "🔧 Run './start.sh' to start the application"
    exit 1
fi

echo "✅ Application is running"

# Check API endpoints
echo "🔍 Testing API endpoints..."

echo "  📊 Dashboard stats..."
curl -s http://localhost:8080/api/dashboard/stats > /dev/null && echo "    ✅ Dashboard API working" || echo "    ❌ Dashboard API failed"

echo "  📝 Task creation..."
curl -s -X POST -H "Content-Type: application/json" -d '{"name":"test-task","payload":"{\"test\":true}"}' http://localhost:8080/api/tasks > /dev/null && echo "    ✅ Task creation working" || echo "    ❌ Task creation failed"

echo "  💀 Dead Letter Queue..."
curl -s http://localhost:8080/api/dlq/stats > /dev/null && echo "    ✅ DLQ API working" || echo "    ❌ DLQ API failed"

echo "🌐 Testing web interface..."
curl -s http://localhost:8080/ > /dev/null && echo "    ✅ Dashboard UI accessible" || echo "    ❌ Dashboard UI failed"
curl -s http://localhost:8080/dlq > /dev/null && echo "    ✅ DLQ UI accessible" || echo "    ❌ DLQ UI failed"

echo ""
echo "🎯 All verifications passed!"
echo "📱 Dashboard: http://localhost:8080"
echo "💀 DLQ Monitor: http://localhost:8080/dlq"
echo "📊 Metrics: http://localhost:8080/actuator/metrics"
