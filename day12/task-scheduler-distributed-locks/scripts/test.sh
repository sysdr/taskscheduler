#!/bin/bash

echo "🧪 Running Tests"
echo "==============="

echo "Running unit and integration tests..."
./mvnw test

if [ $? -eq 0 ]; then
    echo "✅ All tests passed"
else
    echo "❌ Some tests failed"
    exit 1
fi
