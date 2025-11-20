#!/bin/bash
set -e

echo "========================================="
echo "Building Task Scheduler Metrics Project"
echo "========================================="

# Check Java version
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "Java version: $JAVA_VERSION"

if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "Warning: Java 21+ recommended for virtual threads"
fi

# Build project
echo "Building with Maven..."
./mvnw clean package -DskipTests

echo ""
echo "Build completed successfully!"
echo "Run ./start.sh to start the application"
