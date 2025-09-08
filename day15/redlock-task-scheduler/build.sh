#!/bin/bash

echo "🏗️  Building Redlock Task Scheduler..."

# Check if Java 21+ is available
if ! java -version 2>&1 | grep -q "21\|22\|23"; then
    echo "❌ Java 21 or higher is required"
    exit 1
fi

# Start Redis cluster
echo "🔴 Starting Redis cluster..."
cd docker
docker-compose up -d
cd ..

# Wait for Redis instances to be ready
echo "⏳ Waiting for Redis instances to be ready..."
sleep 10

# Build the application
echo "🔨 Building application..."
./gradle-8.5/bin/gradle clean build -x test

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "🚀 Starting application..."
    java -jar build/libs/redlock-task-scheduler-1.0.0.jar &
    APP_PID=$!
    echo "Application PID: $APP_PID"
    
    # Wait for application to start
    echo "⏳ Waiting for application to start..."
    sleep 15
    
    # Check if application is running
    if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
        echo "✅ Application is running successfully!"
        echo "🌐 Dashboard: http://localhost:8080"
        echo "📊 Health: http://localhost:8080/actuator/health"
        echo "📈 Metrics: http://localhost:8080/actuator/metrics"
        echo ""
        echo "🧪 Running basic tests..."
        
        # Test API endpoints
        echo "Testing instance info..."
        curl -s http://localhost:8080/api/tasks/instance | grep -q "instanceId" && echo "✅ Instance API working"
        
        echo "Testing tasks API..."
        curl -s http://localhost:8080/api/tasks | grep -q "\[\]" && echo "✅ Tasks API working"
        
        echo ""
        echo "🎉 Redlock Task Scheduler is ready!"
        echo "Press Ctrl+C to stop the application"
        
        # Keep running
        wait $APP_PID
    else
        echo "❌ Application failed to start properly"
        kill $APP_PID 2>/dev/null
        exit 1
    fi
else
    echo "❌ Build failed!"
    exit 1
fi
