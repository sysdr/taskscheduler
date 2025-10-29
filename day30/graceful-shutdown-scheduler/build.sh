#!/bin/bash

set -e

echo "🔨 Building Graceful Shutdown Task Scheduler..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java 21 is available
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 21 ]; then
    echo "❌ Java 21 or higher is required. Current version: $java_version"
    exit 1
fi

echo "✅ Java version check passed"

# Clean and compile
echo "🧹 Cleaning previous builds..."
mvn clean

echo "📦 Compiling and packaging..."
mvn package -DskipTests

echo "🧪 Running tests..."
mvn test

echo "✅ Build completed successfully!"
echo "📦 JAR file created: target/graceful-shutdown-scheduler-1.0.0.jar"
