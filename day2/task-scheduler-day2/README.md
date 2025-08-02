# TaskScheduler Pro - Enterprise Dashboard

A professional, enterprise-grade task scheduler application built with Spring Boot, featuring a modern web dashboard with full task management capabilities.

## 🚀 Features

### Core Functionality
- **Task Management**: Create, edit, pause, resume, and delete scheduled tasks
- **Multiple Scheduling Types**: Fixed Rate, Fixed Delay, and Cron expressions
- **Real-time Monitoring**: Live dashboard with metrics and execution history
- **Professional UI**: Modern, responsive design similar to enterprise tools

### Dashboard Features
- **Professional Navigation**: Dark sidebar with intuitive navigation
- **Interactive Charts**: Real-time data visualization with Chart.js
- **Mobile Responsive**: Works seamlessly on all devices
- **Keyboard Shortcuts**: Quick access to search and navigation
- **Real-time Updates**: Auto-refreshing dashboard every 10 seconds

### Task Management
- **Task Creation**: Modal-based task creation with validation
- **Task Types**: Support for Fixed Rate, Fixed Delay, and Cron scheduling
- **Task Operations**: Edit, pause, resume, execute, and delete tasks
- **Status Tracking**: Visual status indicators and execution history
- **Statistics**: Comprehensive task statistics and metrics

## 🏗️ Architecture

### Backend (Spring Boot)
- **Java 17+** with Spring Boot 3.2.0
- **Maven** for dependency management
- **Spring Web** for REST API endpoints
- **Spring Scheduling** for task execution
- **Thymeleaf** for server-side templating

### Frontend
- **Vanilla JavaScript** with modern ES6+ features
- **Chart.js** for data visualization
- **Font Awesome** for icons
- **CSS3** with custom properties and modern layouts

### Project Structure
```
task-scheduler-day2/
├── src/
│   ├── main/
│   │   ├── java/com/taskscheduler/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── model/           # Data models
│   │   │   ├── service/         # Business logic
│   │   │   └── TaskSchedulerApplication.java
│   │   └── resources/
│   │       ├── static/          # CSS, JS, images
│   │       ├── templates/       # Thymeleaf templates
│   │       └── application.properties
│   └── test/                    # Unit tests
├── start.sh                     # Application startup script
├── stop.sh                      # Application shutdown script
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

## 🛠️ Setup & Installation

### Prerequisites
- **Java 17** or higher
- **Maven 3.6** or higher
- **Git** for version control

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd task-scheduler-day2
   ```

2. **Start the application**
   ```bash
   ./start.sh
   ```

3. **Access the dashboard**
   - Open your browser and navigate to: http://localhost:8080
   - The application will be ready in about 15-20 seconds

### Manual Setup

1. **Build the project**
   ```bash
   mvn clean install
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access endpoints**
   - Dashboard: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health
   - API Documentation: Available via REST endpoints

## 📊 Dashboard Navigation

### Main Sections
- **Dashboard**: Overview with metrics, charts, and recent executions
- **Tasks**: Task management with create, edit, and control operations
- **Schedules**: Calendar view of upcoming scheduled tasks
- **Executions**: Detailed execution history and logs
- **Monitoring**: System performance and health indicators
- **Settings**: Application configuration and preferences

### Keyboard Shortcuts
- `Ctrl/Cmd + K`: Open search modal
- `Esc`: Close modals and sidebar
- `Enter`: Submit forms

## 🔧 API Endpoints

### Task Management
- `GET /api/tasks` - List all tasks
- `POST /api/tasks` - Create new task
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Task Operations
- `POST /api/tasks/{id}/pause` - Pause task
- `POST /api/tasks/{id}/resume` - Resume task
- `POST /api/tasks/{id}/execute` - Execute task manually

### Statistics & Data
- `GET /api/tasks/statistics` - Task statistics
- `GET /api/tasks/active` - Active tasks only
- `GET /api/stats` - Dashboard statistics
- `GET /api/dashboard-data` - Complete dashboard data

## 📝 Task Types

### Fixed Rate
- Runs at fixed intervals regardless of execution time
- Example: `5000` (every 5 seconds)
- Use case: Regular health checks, monitoring tasks

### Fixed Delay
- Runs after a delay from the completion of the previous execution
- Example: `15000` (15 seconds after completion)
- Use case: Cleanup tasks, data processing

### Cron Expression
- Uses standard cron syntax for complex scheduling
- Example: `0 * * * * *` (every minute)
- Use case: Daily reports, maintenance tasks

## 🎨 UI Features

### Professional Design
- **Dark Sidebar**: Enterprise-grade navigation
- **Card-based Layout**: Clean, organized information display
- **Status Indicators**: Color-coded task and system status
- **Responsive Grid**: Adaptive layouts for all screen sizes

### Interactive Elements
- **Hover Effects**: Smooth transitions and feedback
- **Modal Dialogs**: Professional task creation and editing
- **Notifications**: Real-time feedback for user actions
- **Loading States**: Visual feedback during operations

### Mobile Support
- **Collapsible Sidebar**: Hamburger menu on mobile devices
- **Touch-friendly**: Optimized for touch interactions
- **Responsive Tables**: Scrollable data tables on small screens

## 🔍 Monitoring & Health

### System Health
- **Health Checks**: Automatic system monitoring
- **Performance Metrics**: CPU, memory, and disk usage
- **Task Statistics**: Success rates and execution times
- **Error Tracking**: Failed task monitoring and alerts

### Real-time Updates
- **Auto-refresh**: Dashboard updates every 10 seconds
- **Live Metrics**: Real-time task execution counters
- **Status Changes**: Immediate feedback on task state changes

## 🚀 Deployment

### Docker Support
The project includes Docker configuration for containerized deployment:

```bash
# Build and run with Docker
docker-compose up -d

# Stop Docker containers
docker-compose down
```

### Production Considerations
- Configure database for persistent storage
- Set up proper logging and monitoring
- Configure security and authentication
- Set up backup and recovery procedures

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage
- Unit tests for service layer
- Integration tests for controllers
- Frontend JavaScript testing

## 📚 Development

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Include proper documentation and comments
- Maintain consistent formatting

### Git Workflow
1. Create feature branches for new development
2. Write tests for new functionality
3. Ensure all tests pass before merging
4. Update documentation as needed

### Adding New Features
1. Create appropriate model classes
2. Implement service layer business logic
3. Add REST controller endpoints
4. Update frontend JavaScript and CSS
5. Test thoroughly across different devices

## 🐛 Troubleshooting

### Common Issues

**Application won't start**
- Check Java version (requires 17+)
- Verify port 8080 is available
- Check application logs in `app.log`

**Tasks not executing**
- Verify task scheduling configuration
- Check system resources
- Review task status in dashboard

**Dashboard not loading**
- Clear browser cache
- Check browser console for errors
- Verify all static resources are accessible

### Logs and Debugging
- Application logs: `tail -f app.log`
- Maven logs: Check console output during build
- Browser logs: Use developer tools console

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📞 Support

For questions, issues, or contributions:
- Create an issue in the repository
- Review existing documentation
- Check the troubleshooting section

---

**TaskScheduler Pro** - Professional task scheduling made simple and powerful! 🚀 