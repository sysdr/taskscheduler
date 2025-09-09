#!/bin/bash

echo "🎯 Leader Election Scheduler Demo"
echo "=================================="
echo ""

# Build first
echo "🔨 Building application..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build complete!"
echo ""

# Function to start instance in background
start_instance() {
    local port=$1
    echo "🚀 Starting instance on port $port..."
    SERVER_PORT=$port java -Dserver.port=$port -Dscheduler.instance.id=demo-instance-$port -jar target/*.jar > instance-$port.log 2>&1 &
    local pid=$!
    echo $pid > instance-$port.pid
    echo "   Instance $port PID: $pid"
}

# Start multiple instances
start_instance 8080
sleep 3
start_instance 8081
sleep 3
start_instance 8082

echo ""
echo "🎯 Demo Setup Complete!"
echo "======================="
echo "📊 Dashboard URLs:"
echo "   http://localhost:8080/dashboard"
echo "   http://localhost:8081/dashboard"  
echo "   http://localhost:8082/dashboard"
echo ""
echo "🔍 Watch the logs:"
echo "   tail -f instance-8080.log"
echo "   tail -f instance-8081.log"
echo "   tail -f instance-8082.log"
echo ""
echo "🛑 To stop demo: ./stop-demo.sh"
echo ""
echo "💡 Try killing the leader instance to see failover!"
