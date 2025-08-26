# Enhanced Task Scheduler

A comprehensive task scheduling and execution system built with Spring Boot, featuring advanced task management, execution tracking, and a modern web dashboard.

## Features

- **Task Management**: Create, update, and delete task definitions
- **Multiple Task Types**: Support for calculation, email, database cleanup, and custom tasks
- **Cron Scheduling**: Flexible cron expression support for task scheduling
- **Async Execution**: Asynchronous task execution with thread pool management
- **Result Tracking**: Comprehensive execution result tracking and history
- **Web Dashboard**: Modern, responsive web interface for task management
- **REST API**: Full REST API for integration with other systems
- **H2 Database**: In-memory database with H2 console for development

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 Database (in-memory)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## Project Structure

```
src/
├── main/
│   ├── java/com/ultrascale/scheduler/
│   │   ├── EnhancedTaskSchedulerApplication.java
│   │   ├── config/
│   │   │   └── SchedulerConfig.java
│   │   ├── controller/
│   │   │   ├── DashboardController.java
│   │   │   └── TaskController.java
│   │   ├── model/
│   │   │   ├── TaskDefinition.java
│   │   │   ├── TaskResult.java
│   │   │   ├── TaskStatus.java
│   │   │   └── TaskType.java
│   │   ├── repository/
│   │   │   ├── TaskDefinitionRepository.java
│   │   │   └── TaskResultRepository.java
│   │   ├── service/
│   │   │   ├── EnhancedTaskSchedulerService.java
│   │   │   ├── SampleCalculationTask.java
│   │   │   └── SampleEmailTask.java
│   │   └── wrapper/
│   │       ├── CallableTaskWrapper.java
│   │       ├── RunnableTaskWrapper.java
│   │       └── TaskWrapper.java
│   └── resources/
│       ├── application.properties
│       └── static/
│           └── dashboard.html
└── test/
    └── java/com/ultrascale/scheduler/
        └── EnhancedTaskSchedulerApplicationTests.java
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd enhanced-task-scheduler
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Web Dashboard: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - REST API: http://localhost:8080/api/tasks

### H2 Database Console

- **JDBC URL**: `jdbc:h2:mem:taskdb`
- **Username**: `sa`
- **Password**: `password`

## API Endpoints

### Task Management
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Task Execution
- `POST /api/tasks/{id}/execute` - Execute task
- `GET /api/tasks/{id}/results` - Get task results
- `GET /api/tasks/status/{status}` - Get results by status
- `GET /api/tasks/active` - Get active tasks
- `GET /api/tasks/search?keyword={keyword}` - Search tasks

## Task Types

- **CALCULATION**: Mathematical calculations and computations
- **EMAIL**: Email sending operations
- **DATABASE_CLEANUP**: Database maintenance tasks
- **REPORT_GENERATION**: Report creation and generation
- **DATA_SYNC**: Data synchronization operations
- **CUSTOM**: Custom task implementations

## Configuration

The application can be configured through `application.properties`:

- **Server Port**: `server.port=8080`
- **Thread Pool**: Configurable core and max pool sizes
- **Database**: H2 in-memory database configuration
- **Logging**: Debug level logging for development

## Development

### Adding New Task Types

1. Add new enum value to `TaskType`
2. Implement task logic in a new service class
3. Update `EnhancedTaskSchedulerService.createTaskWrapper()` method
4. Add UI support in the dashboard

### Custom Task Wrappers

The system supports both `Runnable` and `Callable` task wrappers:

- **RunnableTaskWrapper**: For tasks that don't return values
- **CallableTaskWrapper**: For tasks that return values

## Testing

Run the test suite:

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
