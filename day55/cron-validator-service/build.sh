#!/bin/bash
set -e

echo "🔨 Building Cron Validator Service..."

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java 21 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Clean and build
echo "📦 Cleaning previous builds..."
./mvnw clean

echo "🏗️  Compiling and packaging..."
./mvnw package -DskipTests

echo "✅ Build completed successfully!"
echo "📍 JAR location: target/cron-validator-service-1.0.0.jar"
