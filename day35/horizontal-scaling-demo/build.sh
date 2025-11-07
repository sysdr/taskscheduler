#!/bin/bash
set -e

echo "🔨 Building all services..."

cd producer && mvn clean package -DskipTests && cd ..
echo "✅ Producer built successfully"

cd consumer && mvn clean package -DskipTests && cd ..
echo "✅ Consumer built successfully"

cd dashboard && mvn clean package -DskipTests && cd ..
echo "✅ Dashboard built successfully"

echo "🎉 All services built successfully!"
