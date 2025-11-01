#!/bin/bash

echo "🚀 Starting Task Scheduler Message Queue System..."

# Function to wait for service
wait_for_service() {
    local host=$1
    local port=$2
    local service_name=$3
    local timeout=60
    
    echo "⏳ Waiting for $service_name to be ready..."
    for i in $(seq 1 $timeout); do
        if nc -z $host $port 2>/dev/null; then
            echo "✅ $service_name is ready!"
            return 0
        fi
        echo "   Attempt $i/$timeout: $service_name not ready yet..."
        sleep 2
    done
    echo "❌ $service_name failed to start within ${timeout}s"
    return 1
}

# Start Docker services
echo "🐳 Starting Docker services (Kafka, Zookeeper)..."
cd docker && docker-compose up -d

# Wait for services to be ready
wait_for_service localhost 2181 "Zookeeper"
wait_for_service localhost 9092 "Kafka"

# Create Kafka topics
echo "📋 Creating Kafka topics..."
sleep 5
docker exec kafka kafka-topics --create --topic task-executions --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092 --if-not-exists
docker exec kafka kafka-topics --create --topic task-executions-dlq --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092 --if-not-exists

echo "📋 Available topics:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

cd ..

# Start the Spring Boot application
echo "🍃 Starting Spring Boot application..."
java -jar target/task-scheduler-mq-1.0.0.jar &
SPRING_PID=$!

# Wait for Spring Boot to start
wait_for_service localhost 8080 "Spring Boot Application"

echo ""
echo "🎉 System is ready!"
echo ""
echo "🌐 Dashboard: http://localhost:8080/web/static/index.html"
echo "📊 Kafka UI: http://localhost:8081"
echo "🔧 Health Check: http://localhost:8080/actuator/health"
echo "📡 API Base: http://localhost:8080/api"
echo ""
echo "🔄 To stop the system, run: ./stop.sh"
echo ""

# Keep the script running
wait $SPRING_PID
