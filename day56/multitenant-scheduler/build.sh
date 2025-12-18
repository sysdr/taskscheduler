#!/bin/bash
echo "🔨 Building Multi-Tenant Task Scheduler..."

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven first."
    exit 1
fi

mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed!"
    exit 1
fi
