# Day 59: Future Trends in Task Scheduling - Workflow Engine Comparison

## Overview
This implementation compares traditional task schedulers with modern workflow engines (Temporal.io), demonstrating the evolution in distributed task orchestration.

## Features
- **Traditional Scheduler**: Polling-based, manual state management
- **Temporal Workflow**: Event-driven, automatic durability
- **Real-time Dashboard**: Side-by-side comparison
- **Failure Injection**: 15% random failure rate to demonstrate resilience differences
- **Modern UI**: Professional, responsive design

## Quick Start

### Option 1: Simple Mode (No Temporal Server)
```bash
./build.sh
./start.sh
# Choose option 1
```

### Option 2: Full Mode (With Temporal Server)
```bash
# Requires Docker
./build.sh
./start.sh
# Choose option 2
```

## Access Points
- **Dashboard**: http://localhost:8080
- **Temporal UI** (if running): http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## Key Comparisons

### Traditional Scheduler
- ✅ Simple setup
- ✅ Good for periodic tasks
- ❌ Manual retry logic
- ❌ State lost on crashes
- ❌ Complex failure handling

### Temporal Workflow
- ✅ Automatic retry/compensation
- ✅ Durable execution (survives crashes)
- ✅ Built-in observability
- ❌ Complex infrastructure
- ❌ Learning curve

## Real-World Usage
- **Uber**: Ride matching workflows
- **Netflix**: Content encoding pipelines
- **Airbnb**: Booking flows
- **Stripe**: Payment processing

## Testing
1. Create orders using both approaches
2. Observe failure/retry behavior
3. Compare execution histories
4. Note resilience differences

## Architecture Insights
The implementation demonstrates:
- Event sourcing principles
- Saga pattern for compensation
- Durable execution vs polling
- State machine management

## Stop Services
```bash
./stop.sh
```
