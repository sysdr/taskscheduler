# Day 55: Cron Expression Validator & Generator

## Features
- Real-time cron expression validation
- Visual cron expression generator
- Next execution preview
- Multiple timezone support
- Quick templates
- Performance metrics dashboard

## Quick Start

### Local Development
```bash
./build.sh      # Build the project
./start.sh      # Start the service
./test.sh       # Run tests
./stop.sh       # Stop the service
```

### Using Docker
```bash
docker-compose up -d
```

## API Endpoints

### Validate Expression
```bash
curl -X POST http://localhost:8080/api/cron/validate \
  -H "Content-Type: application/json" \
  -d '{
    "expression": "0 9 * * MON",
    "timezone": "UTC",
    "previewCount": 5
  }'
```

### Generate Expression
```bash
curl -X POST http://localhost:8080/api/cron/generate \
  -H "Content-Type: application/json" \
  -d '{
    "type": "DAILY",
    "minute": "0",
    "hour": "9"
  }'
```

## Web Dashboard
Access at: http://localhost:8080

## Testing Examples

Valid Expressions:
- `0 9 * * MON` - Every Monday at 9 AM
- `*/15 * * * *` - Every 15 minutes
- `0 0 1 * *` - First day of every month

Invalid Expressions:
- `0 25 * * *` - Invalid hour (25)
- `0 0 * * 8` - Invalid weekday (8)
- `* * * *` - Missing field

## Architecture
- Spring Boot 3.2.0
- Java 21
- Cron-Utils Library
- Caffeine Cache
- Thymeleaf Templates
