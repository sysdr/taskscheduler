#!/bin/bash
set -e

echo "🏗️  Building Task Scheduler with Leader Failover..."

# Clean and build
echo "📦 Installing dependencies and building..."
mvn clean package -DskipTests

# Copy web resources to target
mkdir -p target/classes/static
cp -r web/static/* target/classes/static/ 2>/dev/null || true
mkdir -p target/classes/templates  
cp web/templates/* target/classes/templates/ 2>/dev/null || true

# Run tests
echo "🧪 Running tests..."
mvn test

echo "✅ Build completed successfully!"
echo ""
echo "🚀 To start the application:"
echo "   ./start.sh"
echo ""
echo "🐳 To run with Docker:"
echo "   ./start.sh docker"
