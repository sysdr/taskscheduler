#!/bin/bash
set -e

echo "🎭 Starting Multi-Instance Demo..."

# Check if Redis is running
if ! redis-cli ping >/dev/null 2>&1; then
    echo "🔴 Starting Redis..."
    docker run -d --name redis-coordination -p 6379:6379 redis:7-alpine
    sleep 3
fi

echo "🚀 Starting 3 application instances..."

# Start instance 1 (port 8080)
SERVER_PORT=8080 java -jar target/spring-cloud-coordination-*.jar &
PID1=$!
sleep 5

# Start instance 2 (port 8081)  
SERVER_PORT=8081 java -jar target/spring-cloud-coordination-*.jar &
PID2=$!
sleep 5

# Start instance 3 (port 8082)
SERVER_PORT=8082 java -jar target/spring-cloud-coordination-*.jar &
PID3=$!
sleep 5

echo ""
echo "✅ Demo environment ready!"
echo "📊 Instance 1: http://localhost:8080"
echo "📊 Instance 2: http://localhost:8081" 
echo "📊 Instance 3: http://localhost:8082"
echo ""
echo "🎯 Open multiple browser tabs to see leader election in action!"
echo "💡 Kill the leader instance to see failover"
echo ""
echo "Press Ctrl+C to stop all instances"

# Cleanup function
cleanup() {
    echo ""
    echo "🛑 Stopping all instances..."
    kill $PID1 $PID2 $PID3 2>/dev/null
    exit 0
}

trap cleanup SIGINT
wait
