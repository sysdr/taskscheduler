#!/bin/bash
set -e

echo "🔨 Building Async Task Scheduler..."

# Check if Java 21 is available
if ! java -version 2>&1 | grep -q "21"; then
    echo "⚠️  Warning: Java 21 not found. This project requires Java 21 for optimal performance."
    echo "Install Java 21 or set JAVA_HOME to continue."
fi

# Make gradlew executable
chmod +x gradlew

# Clean and build
echo "📦 Running Gradle build..."
./gradlew clean build -x test

# Run tests
echo "🧪 Running tests..."
./gradlew test

echo "✅ Build completed successfully!"
echo ""
echo "Next steps:"
echo "1. Run './start.sh' to start the application"
echo "2. Open http://localhost:8080 to view the dashboard"
echo "3. Create sample tasks and watch async execution in real-time"
