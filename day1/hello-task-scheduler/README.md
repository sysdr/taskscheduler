# Hello Task Scheduler - Day 1

Welcome to your first Spring Boot task scheduling application! This project demonstrates the fundamentals of automated task execution using Spring's `@Scheduled` annotation.

## 🚀 Quick Start

### Prerequisites
- **Java 17+** and **Maven** OR **Docker**
- **Git** for version control

### One-Command Setup & Run
```bash
# Clone the repository
git clone <your-repo-url>
cd hello-task-scheduler

# Start with auto-detection (recommended)
./start.sh --auto

# Or choose your method
./start.sh              # Maven (default)
./start.sh --docker     # Docker
```

### Access the Dashboard
- **Web Dashboard**: http://localhost:8080/dashboard
- **API Status**: http://localhost:8080/api/status

## 📋 What This Application Does

- ✅ Prints "Hello from Task Scheduler!" every 10 seconds
- ✅ Includes timestamp with each message  
- ✅ Shows execution counter
- ✅ Automatically shuts down after 60 seconds
- ✅ Expected: Exactly 6 message executions
- ✅ Beautiful web dashboard for real-time monitoring

## 🛠️ Scripts Overview

This project includes comprehensive scripts for easy management:

| Script | Purpose | Usage |
|--------|---------|-------|
| `start.sh` | Build, test, start & demo | `./start.sh [--docker\|--maven\|--auto]` |
| `stop.sh` | Stop & cleanup | `./stop.sh [--force\|--clean\|--docker]` |
| `status.sh` | Check application status | `./status.sh [--app\|--docker\|--logs]` |

### Script Features
- **Multiple Deployment Methods**: Maven and Docker support
- **Auto-detection**: Automatically chooses best available method
- **Interactive Demo**: Real-time logs and web dashboard
- **Comprehensive Testing**: Runs all unit tests
- **Graceful Shutdown**: Safe application termination

📖 **Detailed Script Documentation**: See [SCRIPTS_README.md](SCRIPTS_README.md)

## 🏗️ Project Structure

```
hello-task-scheduler/
├── src/main/java/com/taskscheduler/hello/
│   ├── HelloTaskSchedulerApplication.java    # Main application entry point
│   ├── HelloSchedulerService.java           # Scheduled task implementation
│   └── DashboardController.java             # Web dashboard & API endpoints
├── src/main/resources/
│   └── application.properties               # Configuration
├── src/test/java/
│   └── HelloTaskSchedulerApplicationTests.java
├── scripts/
│   ├── start.sh                            # Start script
│   ├── stop.sh                             # Stop script
│   ├── status.sh                           # Status script
│   └── SCRIPTS_README.md                   # Script documentation
├── pom.xml                                 # Maven dependencies
├── Dockerfile                              # Container configuration
├── .gitignore                              # Git ignore rules
└── README.md                              # This file
```

## 🎯 Expected Output

```
🚀 Starting Hello Task Scheduler Application...
✅ Hello Scheduler Service initialized at 2024-01-15 10:30:00
📋 Will run for 60 seconds, executing every 10 seconds
⏰ Expected executions: 6

🎯 Hello from Task Scheduler! | Execution #1 | Time: 2024-01-15 10:30:10 | Elapsed: 10s
   ↳ This is your first scheduled task execution!
🎯 Hello from Task Scheduler! | Execution #2 | Time: 2024-01-15 10:30:20 | Elapsed: 20s
🎯 Hello from Task Scheduler! | Execution #3 | Time: 2024-01-15 10:30:30 | Elapsed: 30s
🎯 Hello from Task Scheduler! | Execution #4 | Time: 2024-01-15 10:30:40 | Elapsed: 40s
🎯 Hello from Task Scheduler! | Execution #5 | Time: 2024-01-15 10:30:50 | Elapsed: 50s
🎯 Hello from Task Scheduler! | Execution #6 | Time: 2024-01-15 10:31:00 | Elapsed: 60s
   ↳ Final execution - application will shutdown soon!
⏹️  60 seconds elapsed. Shutting down gracefully...
📊 Total executions completed: 6
```

## 🌐 Web Dashboard

The application includes a beautiful web dashboard with:
- **Real-time Status**: Application state and progress
- **Live Statistics**: Execution count, uptime, success rate
- **Configuration Display**: Settings and expected behavior
- **Activity Log**: Real-time execution notifications
- **Progress Tracking**: Visual completion indicator

Access at: http://localhost:8080/dashboard

## 🔧 Development Workflow

### Start Development Session
```bash
./start.sh --auto
```

### Check Status During Development
```bash
./status.sh
```

### Stop When Done
```bash
./stop.sh --clean
```

### View Logs
```bash
# Maven
tail -f app.log

# Docker
docker logs -f hello-scheduler-container
```

## 🐳 Docker Support

### Build and Run with Docker
```bash
./start.sh --docker
```

### Docker Features
- **Multi-stage Build**: Optimized container image
- **Port Mapping**: Exposes on localhost:8080
- **Health Checks**: Verifies container status
- **Automatic Cleanup**: Removes old containers

## 🧪 Testing

### Run Tests
```bash
# Tests run automatically with start.sh
./start.sh

# Or manually
mvn test
```

### Test Coverage
- Unit tests for scheduled task execution
- Integration tests for application startup
- Dashboard API endpoint testing

## 🔑 Key Learning Points

1. **@EnableScheduling**: Activates Spring's scheduling capabilities
2. **@Scheduled(fixedRate)**: Executes task at fixed intervals
3. **Service Components**: Organizing scheduled logic in services
4. **Application Lifecycle**: Handling startup and shutdown events
5. **Web Dashboard**: Real-time monitoring and visualization
6. **Docker Integration**: Containerized deployment

## 🚀 Next Steps

Tomorrow in Day 2, you'll learn about:
- Different scheduling strategies (`fixedRate` vs `fixedDelay`)
- Cron expressions for complex timing
- Multiple scheduled tasks
- Error handling in scheduled tasks
- Advanced dashboard features

## 🛠️ Troubleshooting

### Common Issues

**Application doesn't start**: 
```bash
# Check Java version
java -version

# Check Maven
mvn -version

# Check Docker
docker --version
```

**Port conflicts**: 
```bash
# Check what's using port 8080
lsof -i :8080

# Stop conflicting process
./stop.sh --force
```

**Build failures**: 
```bash
# Clean and rebuild
mvn clean package

# Or use Docker
./start.sh --docker
```

**Permission denied**: 
```bash
# Make scripts executable
chmod +x *.sh
```

## 📝 Git Setup

This project is ready for git with:
- ✅ Comprehensive `.gitignore` file
- ✅ Clean project structure
- ✅ Documentation included
- ✅ Scripts for easy management

### Initial Git Setup
```bash
git init
git add .
git commit -m "Initial commit: Hello Task Scheduler with comprehensive scripts"
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with `./start.sh --auto`
5. Commit and push
6. Create a pull request

## 📄 License

This project is part of the Task Scheduler Course.

---

**Happy Scheduling! 🕐**

*Ready for Day 2: Advanced Scheduling Strategies*
