#!/bin/bash

echo "Building all services..."

cd scheduler-service
mvn clean package -DskipTests
cd ..

cd payment-service
mvn clean package -DskipTests
cd ..

cd notification-service
mvn clean package -DskipTests
cd ..

echo "Build completed successfully!"
