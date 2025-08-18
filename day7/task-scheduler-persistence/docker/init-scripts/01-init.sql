-- Create additional database objects if needed
-- This script runs when PostgreSQL container starts for the first time

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE taskscheduler TO taskscheduler;
GRANT ALL PRIVILEGES ON SCHEMA public TO taskscheduler;
