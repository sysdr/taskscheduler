#!/bin/bash

PORT=${1:-8080}

echo "🚀 Starting Leader Election Scheduler on port $PORT..."
echo "📊 Dashboard will be available at: http://localhost:$PORT/dashboard"
echo "🏥 Health check at: http://localhost:$PORT/actuator/health"
echo "🗃️ H2 Console at: http://localhost:$PORT/h2-console"
echo ""
echo "To test with multiple instances:"
echo "  Terminal 1: ./start.sh 8080"
echo "  Terminal 2: ./start.sh 8081"
echo "  Terminal 3: ./start.sh 8082"
echo ""

export SERVER_PORT=$PORT
java -Dserver.port=$PORT -Dscheduler.instance.id=$(hostname)-$PORT -jar target/*.jar
