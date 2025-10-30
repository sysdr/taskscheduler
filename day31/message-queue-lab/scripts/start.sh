#!/bin/bash

echo "🚀 Starting Message Queue Lab Environment..."

# Start Docker containers
echo "📦 Starting Docker containers..."
cd docker && docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check if Kafka is ready
echo "🔍 Checking Kafka availability..."
for i in {1..10}; do
    if docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list > /dev/null 2>&1; then
        echo "✅ Kafka is ready!"
        break
    fi
    echo "⏳ Waiting for Kafka... ($i/10)"
    sleep 5
done

# Check if RabbitMQ is ready
echo "🔍 Checking RabbitMQ availability..."
for i in {1..10}; do
    if curl -f -u admin:admin123 http://localhost:15672/api/overview > /dev/null 2>&1; then
        echo "✅ RabbitMQ is ready!"
        break
    fi
    echo "⏳ Waiting for RabbitMQ... ($i/10)"
    sleep 5
done

cd ..

# Start Spring Boot application
echo "🏃 Starting Spring Boot application..."
java -jar target/message-queue-lab-1.0.0.jar &

# Wait for Spring Boot to start
echo "⏳ Waiting for Spring Boot application..."
sleep 15

echo "🎉 Message Queue Lab is ready!"
echo "📊 Dashboard: http://localhost:8090"
echo "🔧 Kafka UI: http://localhost:8080"
echo "🐰 RabbitMQ Management: http://localhost:15672 (admin/admin123)"
