#!/bin/bash
set -e

echo "🔨 Building Spring Cloud Coordination Project..."

# Clean and compile
echo "📦 Compiling application..."
mvn clean compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package
echo "📦 Packaging application..."
mvn package -DskipTests

echo "✅ Build completed successfully!"
echo "📁 JAR file: target/spring-cloud-coordination-*.jar"
echo "🌐 UI files: ui/"
echo ""
echo "Next steps:"
echo "  ./scripts/start.sh     - Start single instance"
echo "  ./scripts/demo.sh      - Run demo with multiple instances"
echo "  ./scripts/docker-up.sh - Start with Docker"
