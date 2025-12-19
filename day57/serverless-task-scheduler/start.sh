#!/bin/bash

echo "🚀 Starting Serverless Task Scheduler..."

# Start Docker services if not running
docker compose -f docker/docker-compose.yml up -d 2>/dev/null || docker-compose -f docker/docker-compose.yml up -d

# Wait for services
sleep 5

# Run the application
./mvnw spring-boot:run

echo "✅ Application started at http://localhost:8080"
