#!/bin/bash

echo "🔨 Building Task Consumer Service..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Build the application
echo "📦 Compiling and packaging application..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📄 JAR file created: target/task-consumer-service-1.0.0.jar"
else
    echo "❌ Build failed!"
    exit 1
fi

# Build Docker image if Docker is available
if command -v docker &> /dev/null; then
    echo "🐳 Building Docker image..."
    docker build -t task-consumer-service:latest .
    
    if [ $? -eq 0 ]; then
        echo "✅ Docker image built successfully!"
    else
        echo "⚠️ Docker image build failed, but JAR is available"
    fi
else
    echo "⚠️ Docker not found, skipping image build"
fi

echo "🎉 Build process completed!"
