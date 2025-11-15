#!/bin/bash

echo "=================================="
echo "Building Event-Driven Scheduler"
echo "=================================="

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 21"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java 21 or higher required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $JAVA_VERSION"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven"
    exit 1
fi

echo "✅ Maven found"

# Clean and build
echo ""
echo "🔨 Cleaning and building project..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "Next steps:"
    echo "1. Start Kafka: cd docker && docker compose up -d"
    echo "2. Run application: ./start.sh"
else
    echo "❌ Build failed"
    exit 1
fi
