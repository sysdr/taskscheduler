#!/bin/bash

echo "🚀 Starting Enhanced Task Scheduler..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version $JAVA_VERSION is not supported. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java version check passed"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Maven check passed"

# Clean and build the project
echo "🔨 Building the project..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi

echo "✅ Build completed successfully"

# Start the application
echo "🚀 Starting the application..."
mvn spring-boot:run

echo -e "\nApplication is running at: http://localhost:8080"
echo "H2 Console: http://localhost:8080/h2-console"
echo "Press Ctrl+C to stop the application"
