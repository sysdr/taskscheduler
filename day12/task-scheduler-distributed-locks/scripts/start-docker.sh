#!/bin/bash

echo "🐳 Starting Task Scheduler with Docker"
echo "======================================"

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "✅ Docker is running"

# Build and start services
echo "📦 Building and starting services..."
cd docker
docker-compose up --build -d

if [ $? -ne 0 ]; then
    echo "❌ Failed to start services"
    exit 1
fi

echo "✅ Services started successfully"
echo ""
echo "🎯 Access Points:"
echo "  App Instance 1: http://localhost:8080"
echo "  App Instance 2: http://localhost:8081"
echo "  PostgreSQL:     localhost:5432"
echo "  Prometheus:     http://localhost:9090"
echo "  Grafana:        http://localhost:3000 (admin/admin)"
echo ""
echo "📊 Test distributed locking by accessing both instances"
echo "and running the same task simultaneously!"
