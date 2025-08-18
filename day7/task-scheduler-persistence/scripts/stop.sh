#!/bin/bash

echo "🛑 Stopping Task Scheduler Persistence Layer"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Stop Spring Boot application
echo -e "${YELLOW}Stopping Spring Boot application...${NC}"
pkill -f "spring-boot:run" || echo "Spring Boot application not running"

# Stop Docker containers if they exist
if [ -f "docker/docker-compose.yml" ]; then
    echo -e "${YELLOW}Stopping Docker containers...${NC}"
    cd docker
    docker-compose down
    cd ..
fi

echo -e "${GREEN}All services stopped successfully!${NC}"
