#!/bin/bash

echo "🚀 Starting all services..."
echo "Note: Make sure RabbitMQ and Redis are running!"
echo ""

# Start Producer
cd producer
java -jar target/*.jar &
PRODUCER_PID=$!
echo "✅ Producer started (PID: $PRODUCER_PID)"
cd ..

sleep 5

# Start Consumers
cd consumer
CONSUMER_ID=consumer-1 java -jar target/*.jar &
CONSUMER1_PID=$!
echo "✅ Consumer 1 started (PID: $CONSUMER1_PID)"

sleep 2
CONSUMER_ID=consumer-2 java -jar target/*.jar &
CONSUMER2_PID=$!
echo "✅ Consumer 2 started (PID: $CONSUMER2_PID)"

sleep 2
CONSUMER_ID=consumer-3 java -jar target/*.jar &
CONSUMER3_PID=$!
echo "✅ Consumer 3 started (PID: $CONSUMER3_PID)"
cd ..

sleep 5

# Start Dashboard
cd dashboard
java -jar target/*.jar &
DASHBOARD_PID=$!
echo "✅ Dashboard started (PID: $DASHBOARD_PID)"
cd ..

echo ""
echo "🎉 All services started!"
echo "📊 Dashboard: http://localhost:8082"
echo "📤 Producer API: http://localhost:8080"
echo "🐰 RabbitMQ Management: http://localhost:15672"
echo ""
echo "PIDs: Producer=$PRODUCER_PID, Consumers=$CONSUMER1_PID,$CONSUMER2_PID,$CONSUMER3_PID, Dashboard=$DASHBOARD_PID"
echo "Save these PIDs to kill processes later"
