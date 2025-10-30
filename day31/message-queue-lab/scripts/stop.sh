#!/bin/bash

echo "🛑 Stopping Message Queue Lab Environment..."

# Stop Spring Boot application
echo "🏃 Stopping Spring Boot application..."
pkill -f "message-queue-lab-1.0.0.jar"

# Stop Docker containers
echo "📦 Stopping Docker containers..."
cd docker && docker-compose down

echo "✅ Message Queue Lab stopped!"
