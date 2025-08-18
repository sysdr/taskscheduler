#!/bin/bash

set -e

echo "🚀 Starting Task Scheduler Persistence Layer"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PROFILE=${1:-dev}

if [ "$PROFILE" = "prod" ]; then
    echo -e "${BLUE}Starting in PRODUCTION mode with PostgreSQL${NC}"
    
    # Check if Docker is running
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}Docker is not running. Please start Docker first.${NC}"
        exit 1
    fi
    
    # Start PostgreSQL with Docker Compose
    echo -e "${YELLOW}Starting PostgreSQL database...${NC}"
    cd docker
    docker-compose up -d postgres
    
    echo -e "${YELLOW}Waiting for PostgreSQL to be ready...${NC}"
    sleep 10
    
    # Wait for PostgreSQL to be ready
    until docker-compose exec -T postgres pg_isready -U taskscheduler -d taskscheduler; do
        echo -e "${YELLOW}Waiting for PostgreSQL...${NC}"
        sleep 2
    done
    
    echo -e "${GREEN}PostgreSQL is ready!${NC}"
    
    # Start Adminer for database management
    docker-compose up -d adminer
    echo -e "${GREEN}Adminer is available at http://localhost:8081${NC}"
    echo -e "${BLUE}PostgreSQL connection details:${NC}"
    echo -e "  Server: postgres"
    echo -e "  Username: taskscheduler"
    echo -e "  Password: taskscheduler123"
    echo -e "  Database: taskscheduler"
    
    cd ..
else
    echo -e "${BLUE}Starting in DEVELOPMENT mode with H2 database${NC}"
fi

# Install dependencies and build
echo -e "${YELLOW}Installing dependencies and building application...${NC}"
./mvnw clean install -DskipTests

# Run the application
echo -e "${YELLOW}Starting Spring Boot application...${NC}"
if [ "$PROFILE" = "prod" ]; then
    SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
else
    SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
fi
