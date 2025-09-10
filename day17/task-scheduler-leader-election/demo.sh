#!/bin/bash

set -e

echo "=== Task Scheduler Leader Election Demo ==="

echo "Starting services..."
./start.sh &
SERVICES_PID=$!

# Wait for services to start
echo "Waiting for services to initialize..."
sleep 20

echo ""
echo "=== Checking Service Status ==="
echo "Instance 1 Status:"
curl -s http://localhost:8080/api/status | jq '.'

echo ""
echo "Instance 2 Status:"
curl -s http://localhost:8081/api/status | jq '.'

echo ""
echo "=== Monitoring Leader Election (30 seconds) ==="
for i in {1..10}; do
    echo "--- Check $i ---"
    echo "Instance 1 - IsLeader: $(curl -s http://localhost:8080/api/status | jq -r '.isLeader')"
    echo "Instance 2 - IsLeader: $(curl -s http://localhost:8081/api/status | jq -r '.isLeader')"
    sleep 3
done

echo ""
echo "=== Testing Leader Failover ==="
echo "Killing current leader to test failover..."

# Find and kill current leader
LEADER_1=$(curl -s http://localhost:8080/api/status | jq -r '.isLeader')
if [ "$LEADER_1" = "true" ]; then
    echo "Instance 1 is leader, killing it..."
    APP1_PID=$(cat .app1.pid)
    kill $APP1_PID
    rm .app1.pid
else
    echo "Instance 2 is leader, killing it..."
    APP2_PID=$(cat .app2.pid)
    kill $APP2_PID
    rm .app2.pid
fi

echo "Waiting for failover..."
sleep 10

echo "Checking new leadership status:"
if [ -f .app1.pid ]; then
    echo "Remaining Instance 1 - IsLeader: $(curl -s http://localhost:8080/api/status | jq -r '.isLeader')"
fi
if [ -f .app2.pid ]; then
    echo "Remaining Instance 2 - IsLeader: $(curl -s http://localhost:8081/api/status | jq -r '.isLeader')"
fi

echo ""
echo "✅ Demo completed! Check logs for detailed leader election behavior."
echo "Use './stop.sh' to clean up remaining services."

# Clean up
kill $SERVICES_PID 2>/dev/null || true
