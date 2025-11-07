#!/bin/bash

echo "🔍 Checking RabbitMQ and Consumer Connection Status"
echo "=================================================="
echo ""

# Check RabbitMQ
echo "1. Checking RabbitMQ..."
if docker ps | grep -q rabbitmq; then
    echo "   ✅ RabbitMQ container is running"
    RABBITMQ_RUNNING=true
else
    echo "   ❌ RabbitMQ container is not running"
    echo "   💡 Start it with: docker start rabbitmq"
    RABBITMQ_RUNNING=false
fi

# Check if RabbitMQ port is accessible
if nc -z localhost 5672 2>/dev/null; then
    echo "   ✅ RabbitMQ port 5672 is accessible"
else
    echo "   ⚠️  RabbitMQ port 5672 is not accessible"
fi

echo ""
echo "2. Checking Consumers..."
CONSUMER_COUNT=$(ps aux | grep "task-consumer" | grep -v grep | wc -l)
if [ "$CONSUMER_COUNT" -gt 0 ]; then
    echo "   ✅ Found $CONSUMER_COUNT consumer(s) running"
    ps aux | grep "task-consumer" | grep -v grep | awk '{print "   - PID:", $2, "|", $11, $12, $13}'
else
    echo "   ❌ No consumers running"
    echo "   💡 Start consumers with: ./start.sh"
fi

echo ""
echo "3. How Consumers Fetch from RabbitMQ:"
echo "   ┌─────────────────────────────────────────────────┐"
echo "   │  Message Flow:                                  │"
echo "   │                                                  │"
echo "   │  1. Producer → Sends task to RabbitMQ queue     │"
echo "   │     (task.queue)                                │"
echo "   │                                                  │"
echo "   │  2. RabbitMQ → Stores message in queue         │"
echo "   │                                                  │"
echo "   │  3. Consumer → Listens to queue automatically   │"
echo "   │     (@RabbitListener annotation)               │"
echo "   │                                                  │"
echo "   │  4. Consumer → Processes task when received     │"
echo "   │     - Updates Redis stats                       │"
echo "   │     - Simulates work (sleep)                    │"
echo "   │     - Marks task as COMPLETED                   │"
echo "   └─────────────────────────────────────────────────┘"
echo ""

echo "4. Consumer Configuration:"
echo "   - Queue Name: task.queue"
echo "   - Connection: localhost:5672"
echo "   - Username: guest"
echo "   - Auto-connect: Yes (Spring Boot auto-configuration)"
echo ""

if [ "$RABBITMQ_RUNNING" = true ] && [ "$CONSUMER_COUNT" -gt 0 ]; then
    echo "5. Testing Connection..."
    echo "   Producing a test task..."
    curl -s -X POST "http://localhost:8080/api/producer/batch?count=1" > /dev/null 2>&1
    sleep 2
    
    # Check if task was processed
    QUEUE_DEPTH=$(curl -s http://localhost:15672/api/queues/%2F/task.queue -u guest:guest 2>/dev/null | grep -o '"messages":[0-9]*' | grep -o '[0-9]*' || echo "0")
    if [ "$QUEUE_DEPTH" = "0" ] || [ -z "$QUEUE_DEPTH" ]; then
        echo "   ✅ Task was consumed (queue is empty or task processed)"
    else
        echo "   ⚠️  Queue depth: $QUEUE_DEPTH (task may still be processing)"
    fi
    
    echo ""
    echo "✅ System is ready! Consumers will automatically fetch messages from RabbitMQ"
    echo "   when tasks are produced."
else
    echo "⚠️  System not fully ready. Please ensure:"
    [ "$RABBITMQ_RUNNING" != true ] && echo "   - RabbitMQ is running"
    [ "$CONSUMER_COUNT" -eq 0 ] && echo "   - Consumers are running"
fi

echo ""
echo "📊 View dashboard: http://localhost:8082"
echo "🐰 RabbitMQ Management: http://localhost:15672 (guest/guest)"




