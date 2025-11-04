#!/bin/bash

set -e

echo "🏗️ Building Task Scheduler Reliable Consumer..."

# Install dependencies and build
echo "📦 Installing dependencies..."
mvn clean install -DskipTests

echo "🔧 Building application..."
mvn package -DskipTests

echo "🐳 Building Docker images..."
# Start Kafka if not running
cd docker
docker compose up -d
cd ..

# Wait for Kafka to be ready
echo "⏳ Waiting for Kafka to be ready..."
sleep 30

# Create topics
echo "📋 Creating Kafka topics..."
docker exec kafka kafka-topics --create --topic task-execution --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --if-not-exists
docker exec kafka kafka-topics --create --topic task-retry --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --if-not-exists
docker exec kafka kafka-topics --create --topic task-dead-letter --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --if-not-exists

echo "✅ Build completed successfully!"
echo "🌐 Kafka UI available at: http://localhost:8080"
echo "📊 Consumer Dashboard will be available at: http://localhost:8082"
