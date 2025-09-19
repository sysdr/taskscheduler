#!/bin/bash

echo "🔨 Building Task Execution Tracker..."

# Check Java version
java -version

# Clean and build
echo "📦 Building with Maven..."
mvn clean package -DskipTests

echo "✅ Build completed successfully!"
