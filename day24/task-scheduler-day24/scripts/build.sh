#!/bin/bash

echo "🔨 Building Task Scheduler Day 24..."

# Clean and compile
echo "📦 Cleaning and compiling..."
mvn clean compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package application
echo "📦 Packaging application..."
mvn package -DskipTests

echo "✅ Build completed successfully!"
echo "📁 JAR file: target/task-scheduler-day24-1.0.0.jar"
