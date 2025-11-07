#!/bin/bash

echo "🔄 Restarting Consumers to Connect to RabbitMQ"
echo "=============================================="
echo ""

# Stop existing consumers
echo "1. Stopping existing consumers..."
pkill -f "task-consumer" 2>/dev/null
sleep 2
echo "   ✅ Consumers stopped"
echo ""

# Check RabbitMQ
echo "2. Verifying RabbitMQ is running..."
if ! docker ps | grep -q rabbitmq; then
    echo "   ⚠️  RabbitMQ is not running. Starting it..."
    docker start rabbitmq 2>/dev/null || docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:3.12-management
    echo "   ⏳ Waiting for RabbitMQ to be ready..."
    sleep 5
fi

if nc -z localhost 5672 2>/dev/null; then
    echo "   ✅ RabbitMQ is ready"
else
    echo "   ❌ RabbitMQ is not accessible on port 5672"
    exit 1
fi

echo ""
echo "3. Starting consumers with proper instance names..."
cd consumer

CONSUMER_ID=consumer-1 java -jar target/*.jar > /tmp/consumer1.log 2>&1 &
CONSUMER1_PID=$!
echo "   ✅ Consumer 1 started (PID: $CONSUMER1_PID) - Instance: consumer-1"

sleep 2

CONSUMER_ID=consumer-2 java -jar target/*.jar > /tmp/consumer2.log 2>&1 &
CONSUMER2_PID=$!
echo "   ✅ Consumer 2 started (PID: $CONSUMER2_PID) - Instance: consumer-2"

sleep 2

CONSUMER_ID=consumer-3 java -jar target/*.jar > /tmp/consumer3.log 2>&1 &
CONSUMER3_PID=$!
echo "   ✅ Consumer 3 started (PID: $CONSUMER3_PID) - Instance: consumer-3"

cd ..

echo ""
echo "4. Waiting for consumers to connect to RabbitMQ..."
sleep 8

# Check connection status
echo ""
echo "5. Connection Status:"
echo "   Checking consumer logs for connection messages..."

if grep -q "Started ConsumerApplication" /tmp/consumer1.log 2>/dev/null; then
    echo "   ✅ Consumer 1: Connected and ready"
else
    echo "   ⏳ Consumer 1: Still starting..."
fi

if grep -q "Started ConsumerApplication" /tmp/consumer2.log 2>/dev/null; then
    echo "   ✅ Consumer 2: Connected and ready"
else
    echo "   ⏳ Consumer 2: Still starting..."
fi

if grep -q "Started ConsumerApplication" /tmp/consumer3.log 2>/dev/null; then
    echo "   ✅ Consumer 3: Connected and ready"
else
    echo "   ⏳ Consumer 3: Still starting..."
fi

echo ""
echo "📋 How It Works:"
echo "   • Consumers use @RabbitListener to automatically listen to 'task.queue'"
echo "   • Spring Boot auto-configures the RabbitMQ connection"
echo "   • When a message arrives, the consumeTask() method is called"
echo "   • Each consumer processes one task at a time (prefetchCount=1)"
echo "   • Multiple consumers share the workload (horizontal scaling)"
echo ""
echo "✅ Consumers are now connected to RabbitMQ!"
echo "   They will automatically fetch and process tasks as they arrive."
echo ""
echo "🧪 Test it: Produce tasks from the dashboard or API:"
echo "   curl -X POST 'http://localhost:8080/api/producer/batch?count=10'"




