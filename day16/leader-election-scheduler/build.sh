#!/bin/bash

echo "🔨 Building Leader Election Scheduler..."

# Clean and build
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "🚀 Run with: ./start.sh"
    echo "🧪 Test with: mvn test"
    echo "📊 Dashboard: http://localhost:8080/dashboard"
else
    echo "❌ Build failed!"
    exit 1
fi
