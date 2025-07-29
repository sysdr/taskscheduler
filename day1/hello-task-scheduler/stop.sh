#!/bin/bash

# Hello Task Scheduler - Stop Script
# This script safely stops the application and cleans up resources

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
PID_FILE="app.pid"
LOG_FILE="app.log"
DOCKER_IMAGE="hello-scheduler"

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
    echo -e "${PURPLE}  $APP_NAME - Stop Script${NC}"
    echo -e "${PURPLE}================================${NC}"
}

print_footer() {
    echo -e "${PURPLE}================================${NC}"
    echo -e "${PURPLE}  Stop Complete!${NC}"
    echo -e "${PURPLE}================================${NC}"
}

# Function to stop application by PID
stop_by_pid() {
    local pid=$1
    local pid_file=$2
    
    print_status "Stopping application with PID: $pid"
    
    # Try graceful shutdown first
    if kill -TERM $pid 2>/dev/null; then
        print_status "Sent SIGTERM signal. Waiting for graceful shutdown..."
        
        # Wait up to 10 seconds for graceful shutdown
        local count=0
        while [ $count -lt 10 ]; do
            if ! kill -0 $pid 2>/dev/null; then
                print_success "Application stopped gracefully"
                rm -f $pid_file
                return 0
            fi
            sleep 1
            count=$((count + 1))
        done
        
        # Force kill if still running
        print_warning "Application didn't stop gracefully. Force killing..."
        if kill -KILL $pid 2>/dev/null; then
            print_success "Application force stopped"
            rm -f $pid_file
            return 0
        else
            print_error "Failed to force stop application"
            return 1
        fi
    else
        print_error "Failed to send SIGTERM to PID $pid"
        return 1
    fi
}

# Function to stop Docker container
stop_docker_container() {
    local container_name=$1
    local pid_file=$2
    
    print_status "Stopping Docker container: $container_name"
    
    # Check if container exists and is running
    if docker ps --filter "name=$container_name" --format "{{.ID}}" | grep -q .; then
        # Try graceful stop first
        if docker stop $container_name; then
            print_status "Container stopped gracefully"
            
            # Remove the container
            if docker rm $container_name; then
                print_success "Container removed successfully"
                rm -f $pid_file
                return 0
            else
                print_warning "Container stopped but could not be removed"
                return 1
            fi
        else
            print_error "Failed to stop container gracefully"
            return 1
        fi
    elif docker ps -a --filter "name=$container_name" --format "{{.ID}}" | grep -q .; then
        # Container exists but not running, just remove it
        print_status "Container is not running, removing..."
        if docker rm $container_name; then
            print_success "Container removed successfully"
            rm -f $pid_file
            return 0
        else
            print_error "Failed to remove container"
            return 1
        fi
    else
        print_warning "Container not found: $container_name"
        rm -f $pid_file
        return 0
    fi
}

# Function to stop Docker containers
stop_docker() {
    print_status "Checking for Docker containers..."
    
    # Stop running containers
    local containers=$(docker ps --filter "ancestor=$DOCKER_IMAGE" --format "{{.ID}}")
    if [ -n "$containers" ]; then
        print_status "Stopping Docker containers..."
        echo "$containers" | xargs docker stop
        print_success "Docker containers stopped"
    else
        print_status "No running Docker containers found"
    fi
    
    # Remove stopped containers
    local stopped_containers=$(docker ps -a --filter "ancestor=$DOCKER_IMAGE" --format "{{.ID}}")
    if [ -n "$stopped_containers" ]; then
        print_status "Removing stopped Docker containers..."
        echo "$stopped_containers" | xargs docker rm
        print_success "Docker containers removed"
    fi
}

# Function to clean up temporary files
cleanup_files() {
    print_status "Cleaning up temporary files..."
    
    # Remove PID file if it exists
    if [ -f "$PID_FILE" ]; then
        rm -f "$PID_FILE"
        print_success "Removed PID file: $PID_FILE"
    fi
    
    # Optionally remove log file
    if [ -f "$LOG_FILE" ]; then
        echo -e "${YELLOW}Log file exists: $LOG_FILE${NC}"
        read -p "Do you want to remove the log file? (y/N): " remove_log
        if [[ $remove_log =~ ^[Yy]$ ]]; then
            rm -f "$LOG_FILE"
            print_success "Removed log file: $LOG_FILE"
        else
            print_status "Log file preserved: $LOG_FILE"
        fi
    fi
}

# Function to show final status
show_final_status() {
    print_status "Final status check..."
    
    # Check if any Java processes are still running
    local java_processes=$(pgrep -f "hello-task-scheduler" || true)
    if [ -n "$java_processes" ]; then
        print_warning "Found remaining Java processes:"
        echo "$java_processes" | while read pid; do
            echo "  PID: $pid - $(ps -p $pid -o cmd=)"
        done
    else
        print_success "No remaining Java processes found"
    fi
    
    # Check if any Docker containers are still running
    local docker_containers=$(docker ps --filter "name=$DOCKER_CONTAINER" --format "{{.ID}}" 2>/dev/null || true)
    if [ -n "$docker_containers" ]; then
        print_warning "Found remaining Docker containers:"
        docker ps --filter "name=$DOCKER_CONTAINER" --format "table {{.ID}}\t{{.Image}}\t{{.Status}}\t{{.Names}}"
    else
        print_success "No remaining Docker containers found"
    fi
    
    # Check if PID file still exists
    if [ -f "$PID_FILE" ]; then
        print_warning "PID file still exists: $PID_FILE"
    fi
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -f, --force    Force stop without graceful shutdown"
    echo "  -c, --clean    Clean up all temporary files including logs"
    echo "  -d, --docker   Stop Docker containers only"
    echo "  -a, --all      Stop everything (default)"
    echo ""
    echo "Examples:"
    echo "  $0              # Stop application gracefully"
    echo "  $0 --force      # Force stop application"
    echo "  $0 --clean      # Stop and clean up all files"
    echo "  $0 --docker     # Stop Docker containers only"
}

# Main execution
main() {
    local force_stop=false
    local clean_all=false
    local docker_only=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -f|--force)
                force_stop=true
                shift
                ;;
            -c|--clean)
                clean_all=true
                shift
                ;;
            -d|--docker)
                docker_only=true
                shift
                ;;
            -a|--all)
                # Default behavior
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
    
    # Stop Docker containers if requested or if no PID file exists
    if [ "$docker_only" = true ] || [ ! -f "$PID_FILE" ]; then
        stop_docker
        if [ "$docker_only" = true ]; then
            print_footer
            exit 0
        fi
    fi
    
    # Stop application by PID if PID file exists
    if [ -f "$PID_FILE" ]; then
        local pid_content=$(cat "$PID_FILE")
        
        if [[ $pid_content == docker:* ]]; then
            # Docker container
            local container_name=$DOCKER_CONTAINER
            stop_docker_container $container_name "$PID_FILE"
        else
            # Maven process
            local pid=$pid_content
            if kill -0 $pid 2>/dev/null; then
                if [ "$force_stop" = true ]; then
                    print_status "Force stopping application..."
                    kill -KILL $pid
                    print_success "Application force stopped"
                    rm -f "$PID_FILE"
                else
                    stop_by_pid $pid "$PID_FILE"
                fi
            else
                print_warning "PID file exists but process is not running"
                rm -f "$PID_FILE"
            fi
        fi
    else
        print_status "No PID file found. Checking for running processes..."
        
        # Check for Docker containers first
        if docker ps --filter "name=$DOCKER_CONTAINER" --format "{{.ID}}" | grep -q .; then
            print_status "Found running Docker container: $DOCKER_CONTAINER"
            stop_docker_container $DOCKER_CONTAINER ""
        else
            # Try to find and stop the application by process name
            local java_pid=$(pgrep -f "hello-task-scheduler" || true)
            if [ -n "$java_pid" ]; then
                print_status "Found running application with PID: $java_pid"
                if [ "$force_stop" = true ]; then
                    kill -KILL $java_pid
                    print_success "Application force stopped"
                else
                    stop_by_pid $java_pid ""
                fi
            else
                print_status "No running application found"
            fi
        fi
    fi
    
    # Clean up files
    if [ "$clean_all" = true ]; then
        cleanup_files
    else
        # Only remove PID file, keep logs
        if [ -f "$PID_FILE" ]; then
            rm -f "$PID_FILE"
        fi
    fi
    
    # Show final status
    show_final_status
    
    print_footer
}

# Handle script interruption
trap 'echo -e "\n${YELLOW}Script interrupted.${NC}"; exit 1' INT

# Run main function
main "$@" 