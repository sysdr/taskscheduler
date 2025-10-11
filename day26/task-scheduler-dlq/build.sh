#!/bin/bash
set -e

echo "🔨 Building Task Scheduler with Dead Letter Queue..."

# Clean and build
echo "📦 Cleaning and compiling..."
mvn clean compile

echo "🧪 Running tests..."
mvn test

echo "📦 Building JAR..."
mvn package -DskipTests

echo "✅ Build completed successfully!"
echo "📋 Run './start.sh' to start the application"
echo "📋 Run './stop.sh' to stop the application"
