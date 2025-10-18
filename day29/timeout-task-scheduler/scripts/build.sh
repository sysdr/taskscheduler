#!/bin/bash
echo "🔨 Building Timeout Task Scheduler..."

# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package -DskipTests

echo "✅ Build completed successfully!"
