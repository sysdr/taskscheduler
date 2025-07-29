#!/bin/bash

# Hello Task Scheduler - Status Script
# This script checks the current status of the application

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
    echo -e "${PURPLE}  $APP_NAME - Status Check${NC}"
    echo -e "${PURPLE}================================${NC}"
}

# Function to check application status
check_app_status() {
    print_status "Checking application status..."
    
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if kill -0 $pid 2>/dev/null; then
            print_success "Application is running (PID: $pid)"
            
            # Get process info
            local process_info=$(ps -p $pid -o pid,ppid,etime,pcpu,pmem,cmd --no-headers)
            echo -e "${CYAN}Process Info:${NC}"
            echo "  $process_info"
            
            # Check uptime
            local uptime=$(ps -p $pid -o etime --no-headers)
            echo -e "${CYAN}Uptime:${NC} $uptime"
            
            return 0
        else
            print_warning "PID file exists but process is not running"
            print_status "Cleaning up stale PID file..."
            rm -f "$PID_FILE"
            return 1
        fi
    else
        # Check if there are any Java processes running
        local java_pid=$(pgrep -f "hello-task-scheduler" || true)
        if [ -n "$java_pid" ]; then
            print_warning "Found running application without PID file (PID: $java_pid)"
            echo -e "${CYAN}Process Info:${NC}"
            ps -p $java_pid -o pid,ppid,etime,pcpu,pmem,cmd --no-headers
            return 0
        else
            print_status "Application is not running"
            return 1
        fi
    fi
}

# Function to check Docker status
check_docker_status() {
    print_status "Checking Docker containers..."
    
    local running_containers=$(docker ps --filter "ancestor=$DOCKER_IMAGE" --format "table {{.ID}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || true)
    local stopped_containers=$(docker ps -a --filter "ancestor=$DOCKER_IMAGE" --format "table {{.ID}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || true)
    
    if [ -n "$running_containers" ]; then
        print_success "Running Docker containers:"
        echo "$running_containers"
    fi
    
    if [ -n "$stopped_containers" ] && [ "$stopped_containers" != "$running_containers" ]; then
        print_warning "Stopped Docker containers:"
        echo "$stopped_containers"
    fi
    
    if [ -z "$running_containers" ] && [ -z "$stopped_containers" ]; then
        print_status "No Docker containers found"
    fi
}

# Function to show log file status
check_log_status() {
    if [ -f "$LOG_FILE" ]; then
        print_status "Log file exists: $LOG_FILE"
        
        # Get file size
        local size=$(du -h "$LOG_FILE" | cut -f1)
        echo -e "${CYAN}Size:${NC} $size"
        
        # Get last modified time
        local modified=$(stat -f "%Sm" "$LOG_FILE" 2>/dev/null || stat -c "%y" "$LOG_FILE" 2>/dev/null || echo "Unknown")
        echo -e "${CYAN}Last Modified:${NC} $modified"
        
        # Show last few lines
        echo -e "${CYAN}Last 5 lines:${NC}"
        tail -5 "$LOG_FILE" 2>/dev/null || echo "  (Unable to read log file)"
    else
        print_status "No log file found"
    fi
}

# Function to show system resources
check_system_resources() {
    print_status "System resource usage..."
    
    # Memory usage
    local mem_info=$(free -h 2>/dev/null || vm_stat 2>/dev/null || echo "Memory info not available")
    echo -e "${CYAN}Memory:${NC}"
    echo "$mem_info" | head -3
    
    # Disk usage
    local disk_usage=$(df -h . | tail -1)
    echo -e "${CYAN}Disk Usage:${NC}"
    echo "  $disk_usage"
    
    # Java processes
    local java_count=$(pgrep -c java 2>/dev/null || echo "0")
    echo -e "${CYAN}Java Processes:${NC} $java_count"
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -a, --app      Check application status only"
    echo "  -d, --docker   Check Docker status only"
    echo "  -l, --logs     Check log file status only"
    echo "  -s, --system   Check system resources only"
    echo "  -f, --full     Full status check (default)"
    echo ""
    echo "Examples:"
    echo "  $0              # Full status check"
    echo "  $0 --app        # Application status only"
    echo "  $0 --docker     # Docker status only"
    echo "  $0 --logs       # Log file status only"
}

# Main execution
main() {
    local check_app=true
    local check_docker=true
    local check_logs=true
    local check_system=true
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -a|--app)
                check_app=true
                check_docker=false
                check_logs=false
                check_system=false
                shift
                ;;
            -d|--docker)
                check_app=false
                check_docker=true
                check_logs=false
                check_system=false
                shift
                ;;
            -l|--logs)
                check_app=false
                check_docker=false
                check_logs=true
                check_system=false
                shift
                ;;
            -s|--system)
                check_app=false
                check_docker=false
                check_logs=false
                check_system=true
                shift
                ;;
            -f|--full)
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
    
    if [ "$check_app" = true ]; then
        check_app_status
        echo ""
    fi
    
    if [ "$check_docker" = true ]; then
        check_docker_status
        echo ""
    fi
    
    if [ "$check_logs" = true ]; then
        check_log_status
        echo ""
    fi
    
    if [ "$check_system" = true ]; then
        check_system_resources
        echo ""
    fi
    
    echo -e "${PURPLE}================================${NC}"
}

# Run main function
main "$@" 