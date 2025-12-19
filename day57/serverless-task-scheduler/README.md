# Day 57: Serverless Task Scheduler with AWS Lambda

## Overview
Task scheduling system with AWS Lambda integration for ad-hoc task execution, demonstrating hybrid local/serverless architecture.

## Prerequisites
- Java 21+
- Maven 3.8+
- Docker & Docker Compose

## Quick Start

### Build and Start
```bash
./build.sh
./start.sh
```

### Access Dashboard
Open http://localhost:8080 in your browser

### Stop Services
```bash
./stop.sh
```

## Features

### Core Capabilities
- ✅ Hybrid execution (Local + Lambda)
- ✅ LocalStack Lambda simulation
- ✅ Asynchronous Lambda invocations
- ✅ Automatic retry with exponential backoff
- ✅ Lambda function warming
- ✅ Cost estimation
- ✅ Real-time monitoring dashboard

### Task Execution Modes
- **LOCAL**: Execute in local worker pool
- **LAMBDA**: Execute in AWS Lambda
- **AUTO**: Scheduler decides based on load

## Testing

### Create Single Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Process Image",
    "type": "image-processing",
    "executionMode": "LAMBDA",
    "functionName": "image-processor",
    "payload": {"imageUrl": "https://example.com/img.jpg"}
  }'
```

### Create Bulk Tasks
Click "Create 10 Tasks" button in the dashboard

### View Statistics
```bash
curl http://localhost:8080/api/tasks/stats
```

### Warm Lambda Pool
```bash
curl -X POST http://localhost:8080/api/tasks/warm-pool
```

## Architecture

### Components
1. **Task Scheduler Service**: Core orchestration
2. **Lambda Executor Service**: AWS Lambda invocation
3. **Task Repository**: H2 database persistence
4. **Redis**: Task state tracking
5. **LocalStack**: Local Lambda simulation

### Data Flow
1. Task created via API
2. Scheduler evaluates execution mode
3. Lambda Executor invokes function (async)
4. Lambda executes and returns result
5. Callback updates task status
6. Dashboard reflects changes

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Lambda Configuration
lambda.image-processor.function-name=image-processor
lambda.image-processor.timeout=300

# Scheduler Configuration
scheduler.local-worker-pool-size=10
scheduler.lambda-task-threshold=5
scheduler.enable-warm-pool=true
```

## Monitoring

### Dashboard Metrics
- Total tasks created
- Completed/Running/Failed counts
- Lambda execution costs
- Task execution times
- Retry statistics

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Assignment Solution Hints

### Step Functions Integration
1. Add AWS Step Functions SDK dependency
2. Create `StepFunctionsExecutor` service
3. Design workflow state machine JSON
4. Implement workflow status polling
5. Handle partial failure scenarios

### Cross-Region Execution
1. Configure multiple AWS region endpoints
2. Add region routing logic based on task metadata
3. Implement region-aware S3 presigned URLs
4. Monitor cross-region latency
5. Calculate data transfer costs

## Troubleshooting

### LocalStack Not Starting
```bash
docker-compose -f docker/docker-compose.yml logs localstack
```

### Lambda Invocation Failures
Check logs for AWS SDK errors:
```bash
grep "Lambda invocation failed" logs/application.log
```

### Redis Connection Issues
```bash
docker-compose -f docker/docker-compose.yml restart redis
```

## Production Deployment

### AWS Configuration
Replace LocalStack endpoint with real AWS:

```properties
aws.endpoint=https://lambda.us-east-1.amazonaws.com
aws.region=us-east-1
aws.accessKeyId=${AWS_ACCESS_KEY_ID}
aws.secretAccessKey=${AWS_SECRET_ACCESS_KEY}
```

### Lambda Function Deployment
```bash
# Package Lambda function
cd src/main/java/com/scheduler/lambda
zip function.zip ImageProcessorHandler.java

# Deploy to AWS
aws lambda create-function \
  --function-name image-processor \
  --runtime java21 \
  --handler com.scheduler.lambda.ImageProcessorHandler::handleRequest \
  --zip-file fileb://function.zip \
  --role arn:aws:iam::ACCOUNT:role/lambda-role
```

## Real-World Applications
- **Stripe**: Fraud detection per transaction
- **Airbnb**: Image processing for property photos
- **Uber**: Post-ride analytics aggregation
- **Netflix**: Thumbnail generation for new content

## Learning Outcomes
✅ AWS Lambda async invocation patterns
✅ Hybrid local/serverless architecture
✅ Cost-optimized task execution
✅ Serverless cold start mitigation
✅ Event-driven task delegation
