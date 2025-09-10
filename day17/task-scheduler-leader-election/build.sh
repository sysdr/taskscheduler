#!/bin/bash

set -e

echo "=== Building Task Scheduler Leader Election Project ==="

# Install MySQL if not running in Docker
if ! command -v mysql &> /dev/null; then
    echo "Installing MySQL..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        brew install mysql
        brew services start mysql
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        sudo apt-get update
        sudo apt-get install -y mysql-server
        sudo systemctl start mysql
    fi
fi

# Build the project
echo "Building with Maven..."
./mvnw clean package -DskipTests

# Run tests
echo "Running tests..."
./mvnw test

# Start MySQL with Docker
echo "Starting MySQL with Docker..."
cd docker
docker-compose up -d mysql

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
sleep 10

echo "✅ Build completed successfully!"
echo "Use './start.sh' to start the application"
echo "Use './stop.sh' to stop all services"
