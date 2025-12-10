#!/bin/bash
echo "=========================================="
echo "Building and Starting Chaos Scheduler"
echo "=========================================="

./build.sh

echo ""
echo "Starting application..."
java -jar target/chaos-scheduler-1.0.0.jar &

sleep 5

echo ""
echo "=========================================="
echo "Chaos Engineering Dashboard is running!"
echo "=========================================="
echo ""
echo "🔥 Dashboard: http://localhost:8050"
echo "📊 Metrics: http://localhost:8050/api/chaos/metrics"
echo "❤️ Health: http://localhost:8050/api/chaos/health"
echo ""
echo "Try these chaos scenarios:"
echo "1. Click 'Kill Leader' to test leader election"
echo "2. Click 'Network Partition' to simulate split-brain"
echo "3. Click 'Inject Latency' to test timeout handling"
echo "4. Click 'Slow Database' to test circuit breakers"
echo ""
echo "Watch metrics update in real-time as chaos unfolds!"
echo "=========================================="
