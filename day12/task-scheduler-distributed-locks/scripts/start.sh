#!/bin/bash

echo "🚀 Starting Task Scheduler with Distributed Locks"
echo "=================================================="

# Check if Java 21 is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21 or later."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java 21 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version check passed"

# Install dependencies and build
echo "📦 Installing dependencies and building application..."
./mvnw clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ Build completed successfully"

# Start application
echo "🚀 Starting application..."
echo "Dashboard will be available at: http://localhost:8080"
echo "H2 Console will be available at: http://localhost:8080/h2-console"
echo "Actuator endpoints at: http://localhost:8080/actuator"

java -jar target/*.jar
