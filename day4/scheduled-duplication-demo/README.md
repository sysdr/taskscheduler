# Day 4: @Scheduled Limitations in Distributed Environment

## 🎯 Learning Objective
Understand why Spring Boot's @Scheduled annotation is problematic in distributed environments and witness duplicate task execution firsthand.

## 🚨 The Problem Demonstrated
This demo shows how @Scheduled tasks execute on EVERY application instance, causing:
- Duplicate business logic execution
- Race conditions and data inconsistency  
- Resource waste and potential customer billing errors
- Violation of "exactly once" execution requirements

## 🚀 Quick Start

### Local Demo (Recommended for Learning)
```bash
# Build and start 3 instances locally
./scripts/start.sh

# Access dashboards
open http://localhost:8080  # Instance 1
open http://localhost:8081  # Instance 2  
open http://localhost:8082  # Instance 3

# Stop all instances
./scripts/stop.sh

# Cleanup build artifacts and logs
./scripts/clean.sh
```

### Docker Demo (Alternative)
```bash
# Build and start 3 Docker containers
./scripts/start.sh docker

# Access dashboards
open http://localhost:8081  # Docker Instance 1
open http://localhost:8082  # Docker Instance 2
open http://localhost:8083  # Docker Instance 3

# Stop containers
./scripts/stop.sh docker

# Cleanup images/volumes (optional)
./scripts/clean.sh docker
```

## 🔍 What to Observe

1. **Dashboard Counters**: Each task shows multiple executions
2. **Error Logs**: Red ERROR messages showing duplicate alerts
3. **Database Records**: Same task executed by different instances
4. **Timing**: Tasks run simultaneously across all instances

## 📊 Problematic Scenarios Demonstrated

### 1. Daily Report Generation (`@Scheduled(cron = "0 */2 * * * *")`)
- **Expected**: 1 report per day
- **Actual**: 3 reports per day (one per instance)
- **Impact**: Duplicate reports, confused stakeholders

### 2. Customer Billing (`@Scheduled(fixedRate = 90000)`)
- **Expected**: Process each customer once
- **Actual**: Process each customer 3 times  
- **Impact**: Triple billing, customer complaints

### 3. Data Cleanup (`@Scheduled(fixedDelay = 120000)`)
- **Expected**: Clean expired data once
- **Actual**: Multiple instances race to clean same data
- **Impact**: Race conditions, potential data corruption

## 🎓 Key Takeaways

1. **@Scheduled is instance-local** - No awareness of other instances
2. **Distributed systems need coordination** - Tasks must be coordinated across instances
3. **Business impact is severe** - Not just inefficiency, but correctness violations
4. **Solution preview**: Next lessons will show proper distributed coordination

## 🛠 Architecture

```
Instance 1 (port 8080) ──┐
                         ├─── All execute same @Scheduled tasks
Instance 2 (port 8081) ──┤     simultaneously (THE PROBLEM!)
                         │
Instance 3 (port 8082) ──┘
```

## 📁 Project Structure
```
scheduled-duplication-demo/
├── src/main/java/com/ultrascale/scheduler/demo/
│   ├── ScheduledDuplicationDemoApplication.java
│   ├── controller/TaskMonitoringController.java
│   ├── service/ProblematicScheduledService.java  # Shows the problem
│   ├── service/TaskExecutionRepository.java
│   └── model/TaskExecutionRecord.java
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
└── scripts/
    ├── start.sh
    └── stop.sh
```

## 🔧 Requirements
- Java 21+
- Maven 3.6+
- Docker (optional, for Docker demo)
- Ports 8080-8083 available

## 📈 Success Criteria
After running this demo, you should:
- ✅ See duplicate task executions in logs
- ✅ Understand why @Scheduled fails in distributed systems
- ✅ Appreciate the need for distributed coordination
- ✅ Be motivated to learn proper solutions (coming in next lessons)

## 🏗 Building Blocks for Next Lessons
This demo establishes the problem that leads to:
- Day 5: Introduction to distributed coordination
- Day 6: Task definition modeling
- Day 7: Leader election patterns
- Day 8: Distributed locks and mutual exclusion

---
**Ultra-Scalable Task Scheduler Implementation with Java Spring Boot**
*Day 4 of 60-lesson comprehensive system design course*
