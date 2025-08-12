#!/bin/bash
set -e

echo "🚀 Starting Day 3: ThreadPoolTaskScheduler Demo..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    echo "On macOS: brew install maven"
    echo "On Ubuntu: sudo apt-get install maven"
    exit 1
fi

# Check if Docker is available (optional)
DOCKER_AVAILABLE=false
if command -v docker &> /dev/null && docker info &> /dev/null; then
    DOCKER_AVAILABLE=true
    echo "✅ Docker is available"
else
    echo "⚠️  Docker not available - using local Java"
fi

echo "📦 Installing dependencies and building..."
mvn clean package -DskipTests

echo "🧪 Running tests..."
mvn test

# Start the application
if [ "$1" = "docker" ] && [ "$DOCKER_AVAILABLE" = true ]; then
    echo "🐳 Starting with Docker Compose..."
    docker-compose up --build -d
    
    echo "⏳ Waiting for services to start..."
    sleep 30
    
    echo "✅ Application started!"
    echo "📊 Dashboard: http://localhost:8080"
    echo "📈 Metrics: http://localhost:8080/actuator/metrics"
    echo "🎯 Prometheus: http://localhost:9090"
    echo "📊 Grafana: http://localhost:3000 (admin/admin)"
    
else
    echo "☕ Starting with local Java..."
    java -jar target/thread-pool-scheduler-1.0.0.jar &
    APP_PID=$!
    echo $APP_PID > app.pid
    
    echo "⏳ Waiting for application to start..."
    sleep 15
    
    echo "✅ Application started!"
    echo "📊 Dashboard: http://localhost:8080"
    echo "📈 Metrics: http://localhost:8080/actuator/metrics"
    echo "🛑 To stop: ./stop.sh"
fi

echo ""
echo "🎯 Demo Instructions:"
echo "1. Open http://localhost:8080 to see the dashboard"
echo "2. Watch the real-time thread pool metrics"
echo "3. Observe how tasks are distributed across multiple threads"
echo "4. Compare thread utilization patterns"
echo "5. Check /actuator/scheduledtasks to see active schedules"
echo ""
echo "📊 Key Metrics to Monitor:"
echo "- Active Threads: Currently executing tasks"
echo "- Pool Size: Total threads in pool"
echo "- Queue Size: Tasks waiting for execution"
echo "- Task execution patterns and thread distribution"
