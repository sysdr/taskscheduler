#!/bin/bash

set -e

echo "🚀 Starting Task Scheduler Reliable Consumer..."

# Start Kafka infrastructure
echo "🐳 Starting Kafka infrastructure..."
cd docker
docker compose up -d
cd ..

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 10

# Start the Spring Boot application
echo "🌟 Starting Consumer Service..."
mvn spring-boot:run &
CONSUMER_PID=$!

echo "✅ Consumer service starting..."
echo "📊 Dashboard: http://localhost:8082"
echo "🔍 Kafka UI: http://localhost:8080"
echo "📈 Metrics: http://localhost:8082/actuator/metrics"

# Wait for consumer to start
sleep 15

# Install Python dependencies for test producer
echo "📦 Installing Python dependencies for test producer..."
pip3 install kafka-python 2>/dev/null || echo "⚠️ kafka-python not installed. Run: pip3 install kafka-python"

echo ""
echo "🎯 To test the consumer, run:"
echo "   python3 test-producer.py"
echo ""
echo "🛑 To stop everything, run:"
echo "   ./stop.sh"

# Keep script running
wait $CONSUMER_PID
