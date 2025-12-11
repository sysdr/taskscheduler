#!/bin/bash
# Stop docker compose stack if available, otherwise kill local java process
if docker compose version &>/dev/null; then
  docker compose down
elif command -v docker-compose &>/dev/null; then
  docker-compose down
else
  pkill -f "java.*dynamic-scaling"
fi
echo "✅ Stopped"
