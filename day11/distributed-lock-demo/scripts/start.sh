#!/bin/bash

echo "🚀 Starting Distributed Lock Problem Demo..."

# Function to check if port is available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo "Port $1 is already in use"
        return 1
    else
        return 0
    fi
}

# Build the project
echo "🔨 Building the project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful!"

# Start multiple instances to demonstrate the race condition
echo "🚀 Starting multiple instances to demonstrate race condition..."

# Instance 1 on port 8080
echo "Starting Instance 1 on port 8080..."
SERVER_PORT=8080 java -jar target/distributed-lock-demo-1.0.0.jar > logs/instance1.log 2>&1 &
INSTANCE1_PID=$!
echo $INSTANCE1_PID > pids/instance1.pid

sleep 5

# Instance 2 on port 8081
echo "Starting Instance 2 on port 8081..."
SERVER_PORT=8081 java -jar target/distributed-lock-demo-1.0.0.jar > logs/instance2.log 2>&1 &
INSTANCE2_PID=$!
echo $INSTANCE2_PID > pids/instance2.pid

sleep 5

# Instance 3 on port 8082
echo "Starting Instance 3 on port 8082..."
SERVER_PORT=8082 java -jar target/distributed-lock-demo-1.0.0.jar > logs/instance3.log 2>&1 &
INSTANCE3_PID=$!
echo $INSTANCE3_PID > pids/instance3.pid

echo "✅ All instances started!"
echo "📊 Instance 1: http://localhost:8080"
echo "📊 Instance 2: http://localhost:8081" 
echo "📊 Instance 3: http://localhost:8082"
echo ""
echo "🔍 Monitor dashboard: http://localhost:8080/api/monitoring/dashboard"
echo "📝 H2 Console: http://localhost:8080/h2-console"
echo "💡 Watch the race condition in action!"
echo ""
echo "⏱️  Wait 30 seconds then check the monitoring endpoints to see duplicate executions"
