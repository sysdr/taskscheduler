#!/bin/bash

echo "🐳 Running Leader Election Scheduler with Docker Compose..."
echo ""

# Build first
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "🔨 Building Docker images..."
docker-compose build

echo "🚀 Starting services..."
docker-compose up -d

echo ""
echo "🎯 Docker Demo Started!"
echo "======================"
echo "📊 Dashboard URLs:"
echo "   http://localhost:8080/dashboard"
echo "   http://localhost:8081/dashboard"
echo "   http://localhost:8082/dashboard"
echo ""
echo "🔍 View logs:"
echo "   docker-compose logs -f scheduler1"
echo "   docker-compose logs -f scheduler2"
echo "   docker-compose logs -f scheduler3"
echo ""
echo "🛑 To stop: docker-compose down"
