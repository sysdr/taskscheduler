#!/bin/bash

echo "🎬 Running Redlock Demo..."

# Start services if not running
if ! curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo "Starting services..."
    ./build.sh &
    sleep 20
fi

echo ""
echo "🔍 Checking Redis instances..."
for port in 6379 6380 6381; do
    if redis-cli -p $port ping 2>/dev/null | grep -q "PONG"; then
        echo "✅ Redis instance on port $port is running"
    else
        echo "❌ Redis instance on port $port is not responding"
    fi
done

echo ""
echo "🧪 Testing Redlock functionality..."

# Create test tasks
echo "Creating test tasks..."
for i in {1..3}; do
    SCHEDULED_TIME=$(date -d "+$i minutes" -Iseconds | cut -d'+' -f1)
    curl -X POST http://localhost:8080/api/tasks \
         -H "Content-Type: application/json" \
         -d "{\"name\":\"Demo Task $i\",\"description\":\"Redlock demo task $i\",\"scheduledTime\":\"$SCHEDULED_TIME\"}" \
         -s > /dev/null
    echo "✅ Created Demo Task $i (scheduled for +$i minutes)"
done

echo ""
echo "📊 Current system status:"
curl -s http://localhost:8080/api/tasks/instance | python3 -m json.tool

echo ""
echo "📋 Current tasks:"
curl -s http://localhost:8080/api/tasks | python3 -m json.tool

echo ""
echo "🌐 Open your browser to view the dashboard:"
echo "   http://localhost:8080"
echo ""
echo "🔧 Test scenarios:"
echo "1. Stop one Redis instance: docker stop redis-redlock-1"
echo "2. Start multiple app instances on different ports"
echo "3. Create tasks and watch them execute with distributed locks"
echo ""
echo "📈 Monitor metrics at: http://localhost:8080/actuator/metrics"
