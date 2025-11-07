#!/bin/bash

echo "🐰 Starting RabbitMQ..."

# Check if RabbitMQ container exists and is running
if docker ps | grep -q rabbitmq; then
    echo "✅ RabbitMQ is already running"
elif docker ps -a | grep -q rabbitmq; then
    echo "🔄 Starting existing RabbitMQ container..."
    docker start rabbitmq
    echo "✅ RabbitMQ started"
else
    echo "🚀 Creating and starting new RabbitMQ container..."
    docker run -d \
        --name rabbitmq \
        -p 5672:5672 \
        -p 15672:15672 \
        -e RABBITMQ_DEFAULT_USER=guest \
        -e RABBITMQ_DEFAULT_PASS=guest \
        rabbitmq:3.12-management
    echo "✅ RabbitMQ container created and started"
fi

echo ""
echo "Waiting for RabbitMQ to be ready..."
sleep 5

# Check if RabbitMQ is healthy
if docker exec rabbitmq rabbitmq-diagnostics -q ping 2>/dev/null; then
    echo "✅ RabbitMQ is ready!"
    echo "📊 Management UI: http://localhost:15672 (guest/guest)"
    echo "🔌 AMQP Port: localhost:5672"
else
    echo "⏳ RabbitMQ is starting... (may take a few more seconds)"
fi




