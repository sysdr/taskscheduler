CREATE DATABASE IF NOT EXISTS task_scheduler;
USE task_scheduler;

CREATE TABLE IF NOT EXISTS leader_election (
    service_name VARCHAR(100) PRIMARY KEY,
    leader_instance_id VARCHAR(255) NOT NULL,
    lease_expires_at TIMESTAMP(6) NOT NULL,
    heartbeat_interval_ms INT NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

-- Create index for performance
CREATE INDEX idx_lease_expires_at ON leader_election(lease_expires_at);
