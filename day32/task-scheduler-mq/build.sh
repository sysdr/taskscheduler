#!/bin/bash

echo "🔧 Building Task Scheduler Message Queue Project..."

# Function to check command existence
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo "❌ $1 is not installed. Please install it first."
        return 1
    fi
    return 0
}

# Check Java
if ! check_command java; then
    echo "Please install Java 17 or later"
    exit 1
fi

# Check Maven
if ! check_command mvn; then
    echo "Please install Apache Maven"
    exit 1
fi

# Check Docker
if ! check_command docker; then
    echo "Please install Docker"
    exit 1
fi

# Clean and compile
echo "🧹 Cleaning previous builds..."
mvn clean

echo "📦 Compiling and packaging..."
mvn compile package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Create topics in Kafka (if running)
    echo "📋 Creating Kafka topics..."
    docker exec -it kafka kafka-topics --create --topic task-executions --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092 2>/dev/null || echo "ℹ️  Topic might already exist or Kafka not running"
    docker exec -it kafka kafka-topics --create --topic task-executions-dlq --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092 2>/dev/null || echo "ℹ️  DLQ topic might already exist or Kafka not running"
    
    echo "🏗️ Build completed successfully!"
else
    echo "❌ Build failed!"
    exit 1
fi
