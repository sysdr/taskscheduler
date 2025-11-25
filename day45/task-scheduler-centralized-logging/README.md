# Day 45: Centralized Log Management with ELK Stack

## Overview
Production-grade centralized logging system for distributed task schedulers using Elasticsearch and custom dashboard.

## Architecture
- **3 Scheduler Instances**: Running on ports 8080, 8081, 8082
- **Elasticsearch**: Stores and indexes all logs
- **Logstash**: Processes and transforms log streams
- **Custom Dashboard**: Modern UI for log search and analysis

## Quick Start

### Build
```bash
./build.sh
```

### Start (with Docker)
```bash
mkdir -p logs
./start.sh
```

### Test
```bash
./test.sh
```

### View Dashboard
Visit `http://localhost:8080/dashboard`

### Stop
```bash
./stop.sh
```

## Features
- ✅ Structured JSON logging
- ✅ Correlation ID tracking
- ✅ Multi-instance aggregation
- ✅ Real-time search
- ✅ Advanced filtering
- ✅ Modern dashboard UI
- ✅ Log statistics

## API Endpoints
- `GET /api/logs/search` - Search logs
- `GET /api/logs/stats` - Get log statistics

## Success Criteria
1. All 3 instances generating logs
2. Logs visible in Elasticsearch
3. Dashboard shows real-time logs
4. Search and filtering works
5. Correlation IDs track requests
