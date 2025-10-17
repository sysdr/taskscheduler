#!/bin/bash

echo "🛑 Stopping Spring Retry Task Scheduler..."

cd docker
docker-compose down

echo "✅ Services stopped!"
