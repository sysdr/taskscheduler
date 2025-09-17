CREATE TABLE task_executions (
    id BIGSERIAL PRIMARY KEY,
    execution_id VARCHAR(64) UNIQUE NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    parameters VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    result VARCHAR(1000),
    error_message VARCHAR(2000),
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_execution_id ON task_executions(execution_id);
CREATE INDEX idx_task_name ON task_executions(task_name);
CREATE INDEX idx_status ON task_executions(status);
CREATE INDEX idx_created_at ON task_executions(created_at DESC);
