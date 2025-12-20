# Day 58: Performance Tuning and Optimization

## Overview
Complete implementation of a task scheduler with intentional bottlenecks for profiling and optimization practice using JVisualVM.

## Quick Start

### Without Docker
```bash
./build.sh
./start.sh
```

Visit: http://localhost:8058

### With Docker
```bash
./build.sh
docker-compose up --build
```

## JVisualVM Setup

1. **Start JVisualVM**:
   ```bash
   jvisualvm
   ```

2. **Connect to Application**:
   - Look for "performance-tuned-scheduler" under Local applications
   - Or manually add JMX connection: `localhost:9010`

3. **Profiling Tabs**:
   - **Monitor**: Watch heap, threads, CPU in real-time
   - **Sampler**: Click "CPU" to see method hotspots
   - **Profiler**: Detailed method timing (adds overhead)
   - **Threads**: View thread states and detect deadlocks

## Testing Scenarios

### 1. Baseline Mode
Clean implementation with reasonable performance.
- Run load test
- Observe metrics
- Profile with JVisualVM

### 2. Bottlenecked Mode
Intentional performance issues:
- ❌ SimpleDateFormat recreation (thread-unsafe, object churn)
- ❌ Unnecessary JSON serialization
- ❌ String concatenation in loops
- ❌ Unbounded collection (memory leak)
- ❌ Synchronized method (contention)

**Expected Issues**:
- High object allocation rate
- Frequent GC pauses
- Lower throughput
- Growing heap usage (memory leak)

### 3. Optimized Mode
All bottlenecks fixed:
- ✅ DateTimeFormatter (reusable, thread-safe)
- ✅ Minimal serialization
- ✅ StringBuilder usage
- ✅ No memory leaks
- ✅ Reduced synchronization

**Expected Improvements**:
- 5-10x throughput increase
- Stable heap usage
- Fewer GC pauses
- Lower CPU per task

## Profiling Workflow

1. **Baseline**: Run baseline mode, note metrics
2. **Profile Bottlenecked**: Switch to bottlenecked mode
   - Run load test
   - Observe degradation
   - Use JVisualVM CPU sampler to find hotspots
   - Take heap dump to find memory leak
3. **Verify Optimized**: Switch to optimized mode
   - Run same load test
   - Compare metrics
   - Confirm improvements

## Key Metrics to Watch

- **Throughput**: Tasks/second
- **Latency**: Task execution time
- **Heap Usage**: Memory consumption pattern
- **GC Frequency**: Garbage collection pauses
- **Thread States**: Running vs waiting vs blocked

## Common Bottleneck Patterns

### Memory Issues
- Sawtooth heap pattern → excessive object creation
- Steady heap growth → memory leak
- High GC pause times → heap pressure

### CPU Issues
- High CPU in specific methods → hotspot
- Low CPU with queued work → I/O or lock contention

### Concurrency Issues
- Many blocked threads → lock contention
- Few running threads → insufficient parallelism

## Optimization Techniques Demonstrated

1. **Object Pooling**: Reuse expensive objects
2. **Efficient String Handling**: StringBuilder vs concatenation
3. **Lazy Initialization**: Create only when needed
4. **Collection Management**: Bounded caches, explicit clearing
5. **Lock Minimization**: Reduce synchronized scope
6. **Modern APIs**: Use thread-safe, efficient alternatives

## Assignment

Profile a previous lesson's scheduler:
1. Enable JMX
2. Generate realistic load
3. Profile with JVisualVM
4. Identify one bottleneck
5. Optimize and measure improvement

Document your findings with before/after metrics!

## Architecture

- **Spring Boot 3.2** with Virtual Threads
- **H2 Database** with HikariCP connection pooling
- **Micrometer + Prometheus** for metrics
- **JMX** for JVisualVM connectivity
- **Modern Responsive UI**

## Endpoints

- `GET /` - Dashboard
- `POST /api/tasks/{mode}` - Submit task (mode: baseline/bottlenecked/optimized)
- `POST /api/load-test/{mode}` - Run 100 concurrent tasks
- `GET /api/metrics` - Current performance metrics
- `GET /actuator/prometheus` - Prometheus metrics

## Stopping

```bash
./stop.sh
```

Or with Docker:
```bash
docker-compose down
```
