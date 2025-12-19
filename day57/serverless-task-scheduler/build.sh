#!/bin/bash

echo "🔨 Building Serverless Task Scheduler..."

# Start LocalStack and Redis
echo "Starting LocalStack and Redis..."
cd docker
docker compose up -d 2>/dev/null || docker-compose up -d
cd ..

# Wait for LocalStack
echo "Waiting for LocalStack to be ready..."
sleep 10

# Build the application
echo "Building Spring Boot application..."
./mvnw clean package -DskipTests

echo "✅ Build complete!"
