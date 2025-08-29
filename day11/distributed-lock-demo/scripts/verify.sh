#!/bin/bash

echo "🧪 Verifying Distributed Lock Problem Demo"
echo "=========================================="

# Check if instances are running
check_instance() {
    local port=$1
    local instance_name=$2
    
    if curl -s http://localhost:$port/actuator/health >/dev/null 2>&1; then
        echo "✅ $instance_name (port $port) is running"
        return 0
    else
        echo "❌ $instance_name (port $port) is not responding"
        return 1
    fi
}

echo "🔍 Checking instance health..."
check_instance 8080 "Instance 1"
check_instance 8081 "Instance 2"  
check_instance 8082 "Instance 3"

echo ""
echo "📊 Checking for race condition evidence..."

# Wait a bit for some executions
sleep 15

# Check for duplicate executions
CRITICAL_COUNT=$(curl -s http://localhost:8080/api/monitoring/recent/CriticalBusinessTask | jq '.executionsInLastMinute')
FINANCIAL_COUNT=$(curl -s http://localhost:8080/api/monitoring/recent/DailyFinancialCalculation | jq '.executionsInLastMinute')

echo "Critical task executions in last minute: $CRITICAL_COUNT"
echo "Financial task executions in last minute: $FINANCIAL_COUNT"

if [ "$CRITICAL_COUNT" -gt 1 ] || [ "$FINANCIAL_COUNT" -gt 1 ]; then
    echo "🎯 SUCCESS: Race condition demonstrated! Multiple instances executed the same task."
    echo "🚨 This shows why distributed locks are essential!"
else
    echo "⏳ Waiting for more executions... Race condition may appear shortly."
fi

echo ""
echo "🌐 Access points:"
echo "   • Dashboard: http://localhost:8080/api/monitoring/dashboard"
echo "   • All executions: http://localhost:8080/api/monitoring/all"
echo "   • H2 Console: http://localhost:8080/h2-console"
