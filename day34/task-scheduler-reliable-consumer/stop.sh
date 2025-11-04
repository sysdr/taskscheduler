#!/bin/bash

echo "🛑 Stopping Task Scheduler Reliable Consumer..."

# Stop Spring Boot application
echo "🌟 Stopping Consumer Service..."
pkill -f "spring-boot:run" || true

# Stop Docker containers
echo "🐳 Stopping Kafka infrastructure..."
cd docker
docker compose down
cd ..

echo "✅ All services stopped"
