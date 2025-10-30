#!/bin/bash

echo "🏗️ Building Message Queue Lab Application..."

# Clean and build with Maven
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Build completed successfully!"
else
    echo "❌ Build failed!"
    exit 1
fi

# Package the application
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Application packaged successfully!"
else
    echo "❌ Packaging failed!"
    exit 1
fi

echo "🎯 Build process completed!"
