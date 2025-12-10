# Day 50: Chaos Engineering for Distributed Task Scheduler

## Overview
A comprehensive chaos testing framework that proactively injects failures to test scheduler resilience.

## Features
- Leader node termination testing
- Network partition simulation
- Latency injection
- Database slowdown testing
- Real-time metrics dashboard
- Automated recovery validation

## Quick Start

### Build and Run
```bash
./run.sh
```

### Access Dashboard
- Dashboard: http://localhost:8050
- Metrics API: http://localhost:8050/api/chaos/metrics
- Health API: http://localhost:8050/api/chaos/health

## Chaos Scenarios

### 1. Leader Kill
Tests leader election and failover:
- Terminates current leader node
- Monitors re-election process
- Validates zero task loss

### 2. Network Partition
Simulates network splits:
- Creates isolated node groups
- Tests split-brain prevention
- Validates consistency

### 3. Latency Injection
Tests timeout handling:
- Adds artificial delays
- Monitors circuit breaker trips
- Validates graceful degradation

### 4. Database Slowdown
Tests resource exhaustion:
- Slows database queries
- Monitors connection pool
- Validates backpressure handling

## Metrics Monitored
- Task throughput
- Error rates
- Leader election frequency
- Average latency
- Circuit breaker trips

## Stop
```bash
./stop.sh
```
