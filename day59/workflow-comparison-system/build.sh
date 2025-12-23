#!/bin/bash

echo "🔨 Building Workflow Comparison System..."

# Clean and build
if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests
elif command -v mvn &> /dev/null; then
    mvn clean package -DskipTests
else
    echo "❌ Error: Maven not found. Please install Maven or add Maven wrapper."
    exit 1
fi

echo "✅ Build complete!"
echo ""
echo "Run './start.sh' to start the application"
