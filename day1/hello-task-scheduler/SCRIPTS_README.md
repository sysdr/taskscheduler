# Hello Task Scheduler - Scripts Guide

This directory contains comprehensive scripts to build, start, test, demonstrate, and stop the Hello Task Scheduler application.

## 📁 Scripts Overview

| Script | Purpose | Description |
|--------|---------|-------------|
| `start.sh` | Start & Demo | Build, test, start, and demonstrate the application |
| `stop.sh` | Stop & Cleanup | Safely stop the application and clean up resources |
| `status.sh` | Status Check | Check application status and system resources |

## 🚀 Quick Start

### 1. Start the Application
```bash
./start.sh              # Use Maven (default)
./start.sh --docker     # Use Docker
./start.sh --auto       # Auto-detect best method
```

This will:
- ✅ Check Java/Maven or Docker installation
- ✅ Clean previous builds
- ✅ Build the application (Maven or Docker)
- ✅ Run all tests
- ✅ Start the application in background
- ✅ Provide interactive demo options

### 2. Check Status
```bash
./status.sh
```

### 3. Stop the Application
```bash
./stop.sh
```

## 📋 Detailed Usage

### Start Script (`start.sh`)

The start script provides a complete workflow from build to demonstration.

#### Features:
- **Multiple Deployment Methods**: Supports Maven and Docker
- **Auto-detection**: Automatically chooses best available method
- **Prerequisites Check**: Verifies Java 17+/Maven or Docker installation
- **Build Process**: Cleans and builds the application
- **Testing**: Runs all unit tests
- **Application Start**: Starts the app in background
- **Interactive Demo**: Offers 4 demo modes including dashboard
- **Real-time Logs**: Shows application output in real-time

#### Demo Modes:
1. **Real-time Logs** (Recommended): Watch logs as they happen
2. **Wait for Completion**: Let the app run and show final summary
3. **Quick Demo**: Show first 10 seconds, then wait for completion
4. **Open Dashboard**: Launch the web dashboard in browser

#### Example Output:
```
================================
  Hello Task Scheduler - Start Script
================================
[INFO] Checking Java installation...
[SUCCESS] Java version: 17.0.2
[INFO] Checking Maven installation...
[SUCCESS] Maven: Apache Maven 3.9.5
[INFO] Cleaning previous builds...
[SUCCESS] Clean completed
[INFO] Building application with Maven...
[SUCCESS] Build completed successfully
[INFO] Running tests...
[SUCCESS] All tests passed!
[INFO] Starting the application...
[SUCCESS] Application started with PID: 12345
[INFO] Logs are being written to: app.log
[SUCCESS] Application is running successfully!
```

#### Docker Support:
```bash
./start.sh --docker     # Use Docker deployment
./start.sh --auto       # Auto-detect best method
```

**Docker Features:**
- **Container Management**: Automatic container creation and cleanup
- **Port Mapping**: Exposes application on localhost:8080
- **Image Building**: Builds optimized Docker image
- **Health Checks**: Verifies container is running properly
- **Dashboard Access**: Web dashboard available at http://localhost:8080/dashboard

**Docker Example Output:**
```
================================
  Hello Task Scheduler - Start Script
================================
[INFO] Using Docker deployment method
[INFO] Checking Docker installation...
[SUCCESS] Docker: Docker version 24.0.5
[SUCCESS] Docker daemon is running
[INFO] Cleaning previous Docker builds...
[SUCCESS] Docker clean completed
[INFO] Building Docker image...
[SUCCESS] Docker build completed successfully
[INFO] Running tests in Docker...
[SUCCESS] All tests passed in Docker!
[INFO] Starting the application with Docker...
[SUCCESS] Docker container started successfully!
[INFO] Container name: hello-scheduler-container
[INFO] Application URL: http://localhost:8080
[INFO] Dashboard URL: http://localhost:8080/dashboard
[SUCCESS] Docker container is running successfully!
```

### Stop Script (`stop.sh`)

The stop script safely terminates the application with multiple options.

#### Features:
- **Graceful Shutdown**: Sends SIGTERM first, waits for graceful shutdown
- **Force Stop**: Option to force kill if needed
- **Docker Support**: Stops Docker containers if running
- **Cleanup Options**: Remove temporary files and logs
- **Process Detection**: Finds and stops processes even without PID file

#### Options:
```bash
./stop.sh              # Graceful stop (default)
./stop.sh --force      # Force stop without graceful shutdown
./stop.sh --clean      # Stop and clean up all files including logs
./stop.sh --docker     # Stop Docker containers only
./stop.sh --help       # Show help
```

#### Example Output:
```
================================
  Hello Task Scheduler - Stop Script
================================
[INFO] Stopping application with PID: 12345
[INFO] Sent SIGTERM signal. Waiting for graceful shutdown...
[SUCCESS] Application stopped gracefully
[INFO] Cleaning up temporary files...
[SUCCESS] Removed PID file: app.pid
[INFO] Final status check...
[SUCCESS] No remaining Java processes found
================================
  Stop Complete!
================================
```

### Status Script (`status.sh`)

The status script provides comprehensive information about the application state.

#### Features:
- **Application Status**: Check if app is running, PID, uptime
- **Docker Status**: Check Docker containers
- **Log File Info**: File size, last modified, recent logs
- **System Resources**: Memory, disk usage, Java processes
- **Modular Checks**: Run specific checks only

#### Options:
```bash
./status.sh            # Full status check (default)
./status.sh --app      # Application status only
./status.sh --docker   # Docker status only
./status.sh --logs     # Log file status only
./status.sh --system   # System resources only
./status.sh --help     # Show help
```

#### Example Output:
```
================================
  Hello Task Scheduler - Status Check
================================
[INFO] Checking application status...
[SUCCESS] Application is running (PID: 12345)
Process Info:
  12345 1 00:02:30 0.5 2.1 java -jar target/hello-task-scheduler-1.0.0.jar
Uptime: 00:02:30

[INFO] Checking Docker containers...
[INFO] No Docker containers found

[INFO] Log file exists: app.log
Size: 4.6K
Last Modified: Jan 15 10:35:30
Last 5 lines:
  🎯 Hello from Task Scheduler! | Execution #3 | Time: 2024-01-15 10:35:30
  🎯 Hello from Task Scheduler! | Execution #4 | Time: 2024-01-15 10:35:40
  🎯 Hello from Task Scheduler! | Execution #5 | Time: 2024-01-15 10:35:50
  🎯 Hello from Task Scheduler! | Execution #6 | Time: 2024-01-15 10:36:00
  ⏹️  60 seconds elapsed. Shutting down gracefully...

[INFO] System resource usage...
Memory:
  MemTotal: 16G
  MemFree: 8G
  MemAvailable: 12G
Disk Usage:
  /dev/disk1s1  500G  200G  300G  40% /
Java Processes: 1
================================
```

## 🔧 Configuration

### Environment Variables
The scripts use these default configurations:
- `APP_NAME`: "Hello Task Scheduler"
- `JAR_NAME`: "hello-task-scheduler-1.0.0.jar"
- `PID_FILE`: "app.pid"
- `LOG_FILE`: "app.log"
- `DOCKER_IMAGE`: "hello-scheduler"
- `DEMO_DURATION`: 70 seconds

### File Structure
```
hello-task-scheduler/
├── start.sh           # Start script
├── stop.sh            # Stop script
├── status.sh          # Status script
├── app.pid            # PID file (created by start.sh)
├── app.log            # Application logs (created by start.sh)
└── SCRIPTS_README.md  # This file
```

## 🎯 Use Cases

### Development Workflow
```bash
# Start development session
./start.sh

# Check status during development
./status.sh

# Stop when done
./stop.sh --clean
```

### Testing
```bash
# Run tests only
./start.sh  # Will run tests as part of startup

# Check test results
./status.sh --logs
```

### Demo/Presentation
```bash
# Start with demo
./start.sh  # Choose demo mode 1 for real-time logs

# Show status during demo
./status.sh --app

# Clean stop after demo
./stop.sh --clean
```

### Web Dashboard
```bash
# Start application
./start.sh --docker  # Docker recommended for dashboard

# Open dashboard in browser
./start.sh  # Choose demo mode 4 to open dashboard

# Or manually open: http://localhost:8080/dashboard
```

### Troubleshooting
```bash
# Check if app is running
./status.sh --app

# Force stop if stuck
./stop.sh --force

# Clean everything and restart
./stop.sh --clean
./start.sh
```

## 🛠️ Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check Java version
java -version

# Check Maven
mvn -version

# Check logs
./status.sh --logs
```

#### Application Won't Stop
```bash
# Force stop
./stop.sh --force

# Check for orphaned processes
./status.sh --app
```

#### Permission Denied
```bash
# Make scripts executable
chmod +x *.sh
```

#### Port Already in Use
```bash
# Check what's using the port
lsof -i :8080

# Stop conflicting process
./stop.sh --force
```

### Log Locations
- **Application Logs**: `app.log`
- **Maven Build Logs**: Console output
- **System Logs**: Check with `./status.sh --system`

## 🔄 Integration with Other Tools

### Docker
```bash
# Build Docker image
docker build -t hello-scheduler .

# Run with Docker
docker run hello-scheduler

# Stop Docker containers
./stop.sh --docker
```

### CI/CD
The scripts can be integrated into CI/CD pipelines:
```yaml
# Example GitHub Actions
- name: Start Application
  run: ./start.sh

- name: Run Tests
  run: ./status.sh --app

- name: Stop Application
  run: ./stop.sh --clean
```

## 📝 Notes

- Scripts are designed for Unix-like systems (Linux, macOS)
- Requires bash shell
- Java 17+ and Maven are prerequisites
- Scripts handle graceful shutdown and cleanup
- All scripts provide colored output for better UX
- Scripts are idempotent (safe to run multiple times)

## 🤝 Contributing

To modify the scripts:
1. Keep the colored output for consistency
2. Maintain error handling and graceful shutdown
3. Update this README if adding new features
4. Test on both Linux and macOS

---

**Happy Scheduling! 🕐** 