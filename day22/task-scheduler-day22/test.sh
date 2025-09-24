#!/bin/bash
echo "🧪 Running Tests..."

if [ -f "mvnw" ]; then
    ./mvnw test
else
    mvn test
fi
