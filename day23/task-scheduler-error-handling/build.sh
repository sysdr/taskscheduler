#!/bin/bash

echo "🔨 Building Task Scheduler Error Handling Implementation..."

# Clean and build
echo "📦 Building with Maven..."
./mvnw clean package -DskipTests

# Run tests
echo "🧪 Running tests..."
./mvnw test

# Build Docker image
echo "🐳 Building Docker image..."
cd docker
docker-compose build

echo "✅ Build completed successfully!"
echo ""
echo "📋 Next steps:"
echo "   1. Run: ./start.sh"
echo "   2. Open: http://localhost:8080"
echo "   3. View H2 Console: http://localhost:8080/h2-console"
