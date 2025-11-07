#!/bin/bash

echo "🛑 Stopping Docker services..."

cd docker
docker-compose down

echo "✅ All Docker services stopped"
