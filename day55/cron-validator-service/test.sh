#!/bin/bash
set -e

echo "🧪 Running Tests..."

./mvnw test

echo "✅ All tests passed!"
