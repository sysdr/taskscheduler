#!/bin/bash

echo "Building Day 38: Batching Task Executions..."
echo "============================================"

# Check Java version
java -version 2>&1 | grep -q "version \"21" || {
    echo "Error: Java 21 is required"
    exit 1
}

# Resolve Maven command (wrapper if available, otherwise system mvn)
if [[ -x "./mvnw" ]]; then
    MVN_CMD="./mvnw"
elif command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
else
    echo "Error: Maven is required but was not found (missing ./mvnw and mvn command)."
    exit 1
fi

# Clean and build
echo "Cleaning previous builds..."
"${MVN_CMD}" clean

echo "Building application..."
"${MVN_CMD}" package -DskipTests

echo ""
echo "✓ Build completed successfully!"
echo "  JAR location: target/batch-execution-1.0.0.jar"
echo ""
echo "Next steps:"
echo "  ./start.sh     - Start the application"
echo "  ./stop.sh      - Stop the application"
