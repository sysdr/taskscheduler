# Dynamic Task Scheduler - Day 9

## Ultra-Scalable Task Scheduler with Java Spring Boot

### Project Overview
This project demonstrates dynamic task creation and scheduling using Spring Boot's TaskScheduler. Unlike static @Scheduled annotations, this implementation reads task definitions from a database and creates schedulers at runtime.

### Features
- ✅ Database-driven task definitions
- ✅ Dynamic task scheduling with cron expressions
- ✅ Real-time task management (start, stop, pause)
- ✅ Live web dashboard
- ✅ RESTful API for task operations
- ✅ Multiple task types (logging, notifications, cleanup, etc.)
- ✅ Task status tracking and execution history

### Quick Start

1. **Start the application:**
   ```bash
   chmod +x start.sh
   ./start.sh
   ```

2. **Access the dashboard:**
   - Web Dashboard: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console
   - Health Check: http://localhost:8080/actuator/health

3. **Stop the application:**
   ```bash
   chmod +x stop.sh
   ./stop.sh
   ```

### API Endpoints

#### Task Management
- `GET /api/tasks` - List all tasks
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

#### Scheduler Control
- `POST /api/scheduler/start/{id}` - Start task
- `POST /api/scheduler/stop/{id}` - Stop task
- `POST /api/scheduler/pause/{id}` - Pause task
- `GET /api/scheduler/status` - System status
- `POST /api/scheduler/reload` - Reload all tasks

### Task Types
- **LOG_MESSAGE**: Simple logging tasks
- **EMAIL_NOTIFICATION**: Email notification tasks
- **DATA_CLEANUP**: Data cleanup operations
- **REPORT_GENERATION**: Report generation tasks
- **SYSTEM_HEALTH_CHECK**: System monitoring tasks

### Sample Cron Expressions
- `0 */5 * * * *` - Every 5 minutes
- `0 0 9 * * MON-FRI` - Weekdays at 9 AM
- `0 0 0 * * *` - Daily at midnight
- `0 0 12 * * SUN` - Sundays at noon

### Technology Stack
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Database access
- **H2 Database** - In-memory database
- **Spring Task Scheduling** - Task execution
- **Spring Web** - REST API
- **Bootstrap-inspired CSS** - UI styling

### Learning Objectives
- Understand dynamic vs static task scheduling
- Learn TaskScheduler interface usage
- Implement database-driven scheduling
- Build real-time task management systems
- Create responsive web dashboards

### Next Steps (Day 10)
Tomorrow we'll enhance this system by implementing Runnable and Callable interfaces for more sophisticated task execution patterns.
