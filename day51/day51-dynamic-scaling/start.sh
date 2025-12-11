#!/bin/bash
# Prefer modern Docker Compose plugin, fall back to legacy binary, otherwise local processes
if command -v docker &>/dev/null; then
  if docker compose version &>/dev/null; then
    DOCKER_BUILDKIT=0 COMPOSE_DOCKER_CLI_BUILD=0 docker compose up -d
  elif command -v docker-compose &>/dev/null; then
    DOCKER_BUILDKIT=0 COMPOSE_DOCKER_CLI_BUILD=0 docker-compose up -d
  else
    echo "Docker is installed but Compose is missing. Install docker-compose plugin/binary or start Redis + app manually."
    exit 1
  fi
  echo "✅ Started! Dashboard: http://localhost:3000"
else
  redis-cli ping &>/dev/null || { echo "Start Redis first"; exit 1; }
  java -jar target/*.jar &
  cd dashboard && python3 -m http.server 3000 &
  echo "✅ Started! Dashboard: http://localhost:3000"
fi
