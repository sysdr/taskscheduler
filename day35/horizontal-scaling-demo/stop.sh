#!/bin/bash

echo "🛑 Stopping all services..."
echo "============================"
echo ""

# Stop Producer
echo "1. Stopping Producer..."
PRODUCER_PIDS=$(ps aux | grep "task-producer" | grep -v grep | awk '{print $2}')
if [ -n "$PRODUCER_PIDS" ]; then
    echo "$PRODUCER_PIDS" | xargs kill 2>/dev/null
    echo "   ✅ Producer stopped"
else
    echo "   ℹ️  Producer not running"
fi

# Stop Consumers
echo ""
echo "2. Stopping Consumers..."
CONSUMER_PIDS=$(ps aux | grep "task-consumer" | grep -v grep | awk '{print $2}')
if [ -n "$CONSUMER_PIDS" ]; then
    COUNT=$(echo "$CONSUMER_PIDS" | wc -l)
    echo "$CONSUMER_PIDS" | xargs kill 2>/dev/null
    echo "   ✅ Stopped $COUNT consumer(s)"
else
    echo "   ℹ️  No consumers running"
fi

# Stop Dashboard
echo ""
echo "3. Stopping Dashboard..."
DASHBOARD_PIDS=$(ps aux | grep "monitoring-dashboard" | grep -v grep | awk '{print $2}')
if [ -n "$DASHBOARD_PIDS" ]; then
    echo "$DASHBOARD_PIDS" | xargs kill 2>/dev/null
    echo "   ✅ Dashboard stopped"
else
    echo "   ℹ️  Dashboard not running"
fi

# Wait a moment for processes to terminate
sleep 2

# Force kill if still running
echo ""
echo "4. Checking for remaining processes..."
REMAINING=$(ps aux | grep -E "(task-producer|task-consumer|monitoring-dashboard)" | grep -v grep)
if [ -n "$REMAINING" ]; then
    echo "   ⚠️  Some processes still running, force killing..."
    ps aux | grep -E "(task-producer|task-consumer|monitoring-dashboard)" | grep -v grep | awk '{print $2}' | xargs kill -9 2>/dev/null
    echo "   ✅ Force killed remaining processes"
else
    echo "   ✅ All processes stopped"
fi

echo ""
echo "✅ All services stopped successfully!"
echo ""
echo "💡 To start again, run: ./start.sh"
