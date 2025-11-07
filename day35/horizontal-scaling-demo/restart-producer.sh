#!/bin/bash

echo "🔨 Rebuilding producer with CORS fix..."

cd producer
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Producer rebuilt successfully"
    echo ""
    echo "🔄 Restarting producer..."
    
    # Find and kill existing producer
    pkill -f "task-producer" 2>/dev/null
    sleep 2
    
    # Start new producer
    cd ..
    cd producer
    java -jar target/*.jar > /tmp/producer.log 2>&1 &
    PRODUCER_PID=$!
    
    echo "✅ Producer restarted (PID: $PRODUCER_PID)"
    echo ""
    echo "📝 CORS is now configured to allow requests from http://localhost:8082"
    echo "🔄 Please refresh your dashboard browser page"
else
    echo "❌ Build failed"
    exit 1
fi




