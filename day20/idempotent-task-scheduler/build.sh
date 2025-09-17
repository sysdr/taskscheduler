#!/bin/bash
set -e

echo "🔧 Building Idempotent Task Scheduler..."

# Check Java version
if ! java -version 2>&1 | grep -q "21\|22\|23"; then
    echo "❌ Java 21+ required. Please install OpenJDK 21 or higher."
    exit 1
fi

# Install dependencies and build
echo "📦 Installing dependencies..."
./mvnw clean install -DskipTests

echo "🧪 Running tests..."
./mvnw test

echo "📦 Building JAR..."
./mvnw package -DskipTests

echo "✅ Build completed successfully!"
echo "🚀 Run './start.sh' to start the application"
