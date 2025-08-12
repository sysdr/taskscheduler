# Architecture Documentation

## Component Interaction Flow

1. **Task Discovery**: SchedulerService polls for eligible tasks every 30 seconds
2. **Lock Acquisition**: ShedLock prevents multiple instances from processing same task
3. **Task Execution**: TaskExecutorService handles async execution with monitoring
4. **State Management**: Task and execution states persisted in database
5. **Monitoring**: Real-time dashboard shows system health and task status

## Database Schema

### task_definitions
- id: Primary key
- name: Unique task identifier  
- description: Human-readable description
- cron_expression: Scheduling pattern
- task_class: Execution handler type
- status: Current task status
- parameters: JSON configuration
- created_at, last_executed_at, next_execution_at: Timestamps
- version: Optimistic locking

### task_executions
- id: Primary key
- task_name: Reference to task definition
- instance_id: Which instance executed the task
- status: Execution outcome
- started_at, completed_at: Execution timeline
- error_message: Failure details
- execution_time_ms: Performance metrics

## Scaling Considerations

- **Horizontal Scaling**: Add more instances, shared Redis/DB state
- **Vertical Scaling**: Increase thread pool sizes and memory
- **Database Optimization**: Connection pooling, read replicas
- **Caching**: Redis for frequent lookups and distributed state
