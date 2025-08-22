-- Create sequence for task definitions
CREATE SEQUENCE IF NOT EXISTS task_definition_seq START WITH 1 INCREMENT BY 1;

-- Create task_definitions table
CREATE TABLE task_definitions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    schedule_expression VARCHAR(100) NOT NULL,
    payload TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    description VARCHAR(500),
    enabled BOOLEAN NOT NULL DEFAULT true,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- Create indexes for better query performance
CREATE INDEX idx_task_definitions_name ON task_definitions(name);
CREATE INDEX idx_task_definitions_type ON task_definitions(type);
CREATE INDEX idx_task_definitions_status ON task_definitions(status);
CREATE INDEX idx_task_definitions_enabled ON task_definitions(enabled);
CREATE INDEX idx_task_definitions_created_at ON task_definitions(created_at);

-- Insert sample data
INSERT INTO task_definitions (name, type, schedule_expression, description, status, enabled, created_by) VALUES
('daily-backup', 'BACKUP', '0 0 2 * * ?', 'Daily database backup task', 'ACTIVE', true, 'system'),
('hourly-cleanup', 'CLEANUP', '0 0 * * * ?', 'Hourly temporary file cleanup', 'ACTIVE', true, 'system'),
('weekly-report', 'REPORT', '0 0 8 ? * MON', 'Weekly analytics report generation', 'INACTIVE', true, 'system'),
('monthly-archive', 'ARCHIVE', '0 0 1 1 * ?', 'Monthly data archival task', 'PAUSED', true, 'system');
