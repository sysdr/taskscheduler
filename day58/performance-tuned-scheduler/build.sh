#!/bin/bash
echo "🔨 Building Performance Tuned Scheduler..."

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven first."
    exit 1
fi

mvn clean package -DskipTests
echo "✅ Build complete!"
