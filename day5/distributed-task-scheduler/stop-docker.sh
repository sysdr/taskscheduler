#!/bin/bash

echo "🛑 Stopping Docker services..."

cd docker
docker-compose down
docker-compose rm -f

echo "✅ All services stopped and removed"
cd ..
