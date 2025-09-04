#!/bin/bash

echo "🚀 Starting Task Scheduler with Redis Distributed Locks..."

# Check if Docker is available and working
if command -v docker &> /dev/null && docker info &> /dev/null && command -v docker-compose &> /dev/null; then
    echo "🐳 Docker detected. Starting with Docker Compose..."
    
    # Build the application
    echo "📦 Building application..."
    mvn clean package -DskipTests
    
    # Start services
    cd docker
    docker-compose up -d
    
    echo "✅ Services started!"
    echo "📊 Dashboard: http://localhost:8080"
    echo "🗄️  H2 Database Console: http://localhost:8080/h2-console"
    echo "🔄 Redis CLI: docker exec -it task-scheduler-redis redis-cli"
    
else
    echo "🔧 Docker not available. Starting with local Redis..."
    
    # Check if Redis is running
    if ! redis-cli ping &> /dev/null; then
        echo "❌ Redis is not running. Please start Redis first:"
        echo "   brew install redis && brew services start redis  (macOS)"
        echo "   sudo apt-get install redis-server && sudo service redis-server start  (Ubuntu)"
        exit 1
    fi
    
    echo "✅ Redis is running"
    
    # Build and run application
    echo "📦 Building application..."
    mvn clean package -DskipTests
    
    echo "🚀 Starting application..."
    java -jar target/redis-distributed-locks-1.0.0.jar
fi
