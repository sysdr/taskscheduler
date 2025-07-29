# Hello Task Scheduler - Day 1

Welcome to your first Spring Boot task scheduling application! This project demonstrates the fundamentals of automated task execution using Spring's `@Scheduled` annotation.

## What This Application Does

- ✅ Prints "Hello from Task Scheduler!" every 10 seconds
- ✅ Includes timestamp with each message  
- ✅ Shows execution counter
- ✅ Automatically shuts down after 60 seconds
- ✅ Expected: Exactly 6 message executions

## Project Structure

```
hello-task-scheduler/
├── src/main/java/com/taskscheduler/hello/
│   ├── HelloTaskSchedulerApplication.java    # Main application entry point
│   └── HelloSchedulerService.java           # Scheduled task implementation
├── src/main/resources/
│   └── application.properties               # Configuration
├── src/test/java/
│   └── HelloTaskSchedulerApplicationTests.java
├── pom.xml                                 # Maven dependencies
├── Dockerfile                              # Container configuration
└── README.md                              # This file
```

## Running the Application

### Method 1: Maven (Recommended)
```bash
mvn spring-boot:run
```

### Method 2: JAR file
```bash
mvn clean package
java -jar target/hello-task-scheduler-1.0.0.jar
```

### Method 3: Docker
```bash
docker build -t hello-scheduler .
docker run hello-scheduler
```

## Expected Output

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

## Key Learning Points

1. **@EnableScheduling**: Activates Spring's scheduling capabilities
2. **@Scheduled(fixedRate)**: Executes task at fixed intervals
3. **Service Components**: Organizing scheduled logic in services
4. **Application Lifecycle**: Handling startup and shutdown events

## Next Steps

Tomorrow in Day 2, you'll learn about:
- Different scheduling strategies (`fixedRate` vs `fixedDelay`)
- Cron expressions for complex timing
- Multiple scheduled tasks
- Error handling in scheduled tasks

## Troubleshooting

**Application doesn't start**: Check Java version (requires JDK 17+)
**No scheduled output**: Verify `@EnableScheduling` is present
**Port conflicts**: Change `server.port` in application.properties
**Build failures**: Ensure Maven is properly installed

Happy scheduling! 🕐
