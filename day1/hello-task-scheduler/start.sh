#!/bin/bash

# Hello Task Scheduler - Start Script
# This script builds, tests, starts, and demonstrates the application
# Supports both Maven and Docker deployment methods

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="Hello Task Scheduler"
JAR_NAME="hello-task-scheduler-1.0.0.jar"
DOCKER_IMAGE="hello-scheduler"
DOCKER_CONTAINER="hello-scheduler-container"
PID_FILE="app.pid"
LOG_FILE="app.log"
DEMO_DURATION=70  # Wait 70 seconds to see the full demo

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${PURPLE}================================${NC}"
    echo -e "${PURPLE}  $APP_NAME - Start Script${NC}"
    echo -e "${PURPLE}================================${NC}"
}

print_footer() {
    echo -e "${PURPLE}================================${NC}"
    echo -e "${PURPLE}  Demo Complete!${NC}"
    echo -e "${PURPLE}================================${NC}"
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -m, --maven    Use Maven build and run (default)"
    echo "  -d, --docker   Use Docker build and run"
    echo "  -a, --auto     Auto-detect best method"
    echo ""
    echo "Examples:"
    echo "  $0              # Use Maven (default)"
    echo "  $0 --docker     # Use Docker"
    echo "  $0 --auto       # Auto-detect"
    echo ""
}

# Function to check if Java is installed
check_java() {
    print_status "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        return 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    print_success "Java version: $JAVA_VERSION"
    return 0
}

# Function to check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        return 1
    fi
    
    MVN_VERSION=$(mvn -version | head -n 1)
    print_success "Maven: $MVN_VERSION"
    return 0
}

# Function to check if Docker is installed
check_docker() {
    print_status "Checking Docker installation..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed or not in PATH"
        return 1
    fi
    
    DOCKER_VERSION=$(docker --version)
    print_success "Docker: $DOCKER_VERSION"
    
    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running"
        return 1
    fi
    
    print_success "Docker daemon is running"
    return 0
}

# Function to auto-detect best method
auto_detect_method() {
    print_status "Auto-detecting best deployment method..."
    
    local java_available=false
    local maven_available=false
    local docker_available=false
    
    if check_java &> /dev/null; then
        java_available=true
    fi
    
    if check_maven &> /dev/null; then
        maven_available=true
    fi
    
    if check_docker &> /dev/null; then
        docker_available=true
    fi
    
    if [ "$docker_available" = true ]; then
        print_success "Docker detected - using Docker deployment"
        return 0  # Docker
    elif [ "$maven_available" = true ] && [ "$java_available" = true ]; then
        print_success "Maven and Java detected - using Maven deployment"
        return 1  # Maven
    else
        print_error "No suitable deployment method found"
        print_status "Please install either Docker or Maven+Java"
        exit 1
    fi
}

# Function to clean previous builds (Maven)
clean_build_maven() {
    print_status "Cleaning previous Maven builds..."
    if [ -f "$PID_FILE" ]; then
        print_warning "Found existing PID file. Stopping previous instance..."
        ./stop.sh
    fi
    
    mvn clean
    print_success "Maven clean completed"
}

# Function to build the application (Maven)
build_app_maven() {
    print_status "Building application with Maven..."
    mvn clean package -DskipTests
    print_success "Maven build completed successfully"
}

# Function to run tests (Maven)
run_tests_maven() {
    print_status "Running tests with Maven..."
    mvn test
    print_success "All tests passed!"
}

# Function to start the application (Maven)
start_app_maven() {
    print_status "Starting the application with Maven..."
    
    # Start the application in background
    nohup java -jar target/$JAR_NAME > $LOG_FILE 2>&1 &
    APP_PID=$!
    
    # Save PID to file
    echo $APP_PID > $PID_FILE
    
    print_success "Application started with PID: $APP_PID"
    print_status "Logs are being written to: $LOG_FILE"
    
    # Wait a moment for the app to start
    sleep 3
    
    # Check if the application is running
    if kill -0 $APP_PID 2>/dev/null; then
        print_success "Application is running successfully!"
    else
        print_error "Application failed to start. Check logs: $LOG_FILE"
        exit 1
    fi
}

# Function to clean previous builds (Docker)
clean_build_docker() {
    print_status "Cleaning previous Docker builds..."
    
    # Stop and remove existing containers
    if docker ps -a --filter "name=$DOCKER_CONTAINER" --format "{{.ID}}" | grep -q .; then
        print_warning "Found existing Docker containers. Stopping..."
        docker stop $DOCKER_CONTAINER 2>/dev/null || true
        docker rm $DOCKER_CONTAINER 2>/dev/null || true
    fi
    
    # Remove old images (optional)
    if docker images $DOCKER_IMAGE --format "{{.ID}}" | grep -q .; then
        print_status "Removing old Docker images..."
        docker rmi $DOCKER_IMAGE 2>/dev/null || true
    fi
    
    print_success "Docker clean completed"
}

# Function to build the application (Docker)
build_app_docker() {
    print_status "Building Docker image..."
    docker build -t $DOCKER_IMAGE .
    print_success "Docker build completed successfully"
}

# Function to run tests (Docker)
run_tests_docker() {
    print_status "Running tests in Docker..."
    
    # Create a temporary container for testing
    docker run --rm --name test-container $DOCKER_IMAGE mvn test
    
    if [ $? -eq 0 ]; then
        print_success "All tests passed in Docker!"
    else
        print_error "Tests failed in Docker"
        exit 1
    fi
}

# Function to start the application (Docker)
start_app_docker() {
    print_status "Starting the application with Docker..."
    
    # Start the container in background
    docker run -d \
        --name $DOCKER_CONTAINER \
        -p 8080:8080 \
        --restart unless-stopped \
        $DOCKER_IMAGE
    
    if [ $? -eq 0 ]; then
        print_success "Docker container started successfully!"
        print_status "Container name: $DOCKER_CONTAINER"
        print_status "Application URL: http://localhost:8080"
        print_status "Dashboard URL: http://localhost:8080/dashboard"
        
        # Save container ID to PID file for compatibility
        CONTAINER_ID=$(docker ps --filter "name=$DOCKER_CONTAINER" --format "{{.ID}}")
        echo "docker:$CONTAINER_ID" > $PID_FILE
        
        # Wait a moment for the app to start
        sleep 5
        
        # Check if the container is running
        if docker ps --filter "name=$DOCKER_CONTAINER" --format "{{.Status}}" | grep -q "Up"; then
            print_success "Docker container is running successfully!"
        else
            print_error "Docker container failed to start"
            docker logs $DOCKER_CONTAINER
            exit 1
        fi
    else
        print_error "Failed to start Docker container"
        exit 1
    fi
}

# Function to show real-time logs
show_logs() {
    print_status "Showing application logs (Press Ctrl+C to stop)..."
    echo -e "${CYAN}=== Application Logs ===${NC}"
    
    if [ -f "$PID_FILE" ] && grep -q "^docker:" "$PID_FILE"; then
        # Docker logs
        docker logs -f $DOCKER_CONTAINER
    else
        # File logs
        tail -f $LOG_FILE
    fi
}

# Function to demonstrate the application
demo_app() {
    print_status "Starting functional demo..."
    print_status "The application will run for 60 seconds and execute 6 scheduled tasks"
    print_status "You can watch the logs in real-time or wait for completion"
    
    echo -e "${YELLOW}Choose demo mode:${NC}"
    echo "1) Watch logs in real-time (recommended)"
    echo "2) Wait for completion and show summary"
    echo "3) Quick demo (show first few logs then wait)"
    echo "4) Open dashboard in browser"
    
    read -p "Enter your choice (1-4): " choice
    
    case $choice in
        1)
            print_status "Starting real-time log viewer..."
            show_logs
            ;;
        2)
            print_status "Waiting for demo completion..."
            sleep $DEMO_DURATION
            print_success "Demo completed! Here's the log summary:"
            echo -e "${CYAN}=== Final Logs ===${NC}"
            if [ -f "$PID_FILE" ] && grep -q "^docker:" "$PID_FILE"; then
                docker logs --tail 20 $DOCKER_CONTAINER
            else
                tail -20 $LOG_FILE
            fi
            ;;
        3)
            print_status "Quick demo mode..."
            print_status "Showing first 10 seconds of logs..."
            if [ -f "$PID_FILE" ] && grep -q "^docker:" "$PID_FILE"; then
                timeout 10s docker logs -f $DOCKER_CONTAINER || true
            else
                timeout 10s tail -f $LOG_FILE || true
            fi
            print_status "Waiting for completion..."
            sleep $((DEMO_DURATION - 10))
            print_success "Demo completed! Here's the summary:"
            echo -e "${CYAN}=== Final Logs ===${NC}"
            if [ -f "$PID_FILE" ] && grep -q "^docker:" "$PID_FILE"; then
                docker logs --tail 10 $DOCKER_CONTAINER
            else
                tail -10 $LOG_FILE
            fi
            ;;
        4)
            print_status "Opening dashboard in browser..."
            if command -v open &> /dev/null; then
                open http://localhost:8080/dashboard
            elif command -v xdg-open &> /dev/null; then
                xdg-open http://localhost:8080/dashboard
            else
                print_status "Please open your browser and go to: http://localhost:8080/dashboard"
            fi
            print_status "Dashboard opened! You can also watch logs in another terminal with:"
            if [ -f "$PID_FILE" ] && grep -q "^docker:" "$PID_FILE"; then
                echo "  docker logs -f $DOCKER_CONTAINER"
            else
                echo "  tail -f $LOG_FILE"
            fi
            ;;
        *)
            print_warning "Invalid choice. Using default mode (real-time logs)..."
            show_logs
            ;;
    esac
}

# Function to show application status
show_status() {
    if [ -f "$PID_FILE" ]; then
        if grep -q "^docker:" "$PID_FILE"; then
            # Docker container
            CONTAINER_ID=$(cat $PID_FILE | cut -d: -f2)
            if docker ps --filter "id=$CONTAINER_ID" --format "{{.Status}}" | grep -q "Up"; then
                print_success "Docker container is running (ID: $CONTAINER_ID)"
                print_status "Container name: $DOCKER_CONTAINER"
                print_status "Application URL: http://localhost:8080"
                print_status "Dashboard URL: http://localhost:8080/dashboard"
                print_status "To stop the application, run: ./stop.sh"
            else
                print_warning "Docker container is not running"
                rm -f $PID_FILE
            fi
        else
            # Maven process
            PID=$(cat $PID_FILE)
            if kill -0 $PID 2>/dev/null; then
                print_success "Application is running (PID: $PID)"
                print_status "Log file: $LOG_FILE"
                print_status "To stop the application, run: ./stop.sh"
            else
                print_warning "PID file exists but application is not running"
                rm -f $PID_FILE
            fi
        fi
    else
        print_warning "Application is not running"
    fi
}

# Main execution
main() {
    local use_docker=false
    local auto_detect=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -d|--docker)
                use_docker=true
                shift
                ;;
            -m|--maven)
                use_docker=false
                shift
                ;;
            -a|--auto)
                auto_detect=true
                shift
                ;;
            *)
                print_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    print_header
    
    # Auto-detect if requested
    if [ "$auto_detect" = true ]; then
        if auto_detect_method; then
            use_docker=true
        else
            use_docker=false
        fi
    fi
    
    if [ "$use_docker" = true ]; then
        print_status "Using Docker deployment method"
        
        # Check Docker prerequisites
        check_docker
        
        # Build and test with Docker
        clean_build_docker
        build_app_docker
        run_tests_docker
        
        # Start application with Docker
        start_app_docker
    else
        print_status "Using Maven deployment method"
        
        # Check Maven prerequisites
        check_java
        check_maven
        
        # Build and test with Maven
        clean_build_maven
        build_app_maven
        run_tests_maven
        
        # Start application with Maven
        start_app_maven
    fi
    
    # Show status
    show_status
    
    # Run demo
    demo_app
    
    print_footer
    print_status "Application is still running in the background"
    print_status "To stop it, run: ./stop.sh"
    
    if [ "$use_docker" = true ]; then
        print_status "To view logs: docker logs -f $DOCKER_CONTAINER"
        print_status "Dashboard: http://localhost:8080/dashboard"
    else
        print_status "To view logs: tail -f $LOG_FILE"
        print_status "Dashboard: http://localhost:8080/dashboard"
    fi
}

# Handle script interruption
trap 'echo -e "\n${YELLOW}Script interrupted. Application may still be running.${NC}"; exit 1' INT

# Run main function
main "$@" 