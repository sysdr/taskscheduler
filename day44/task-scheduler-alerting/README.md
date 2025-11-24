# Day 44: Alerting on Task Failures

## Overview
Production-ready alerting system for task scheduler with multi-channel notifications.

## Features
- Real-time alert evaluation
- Multi-channel notifications (Slack, Email, Webhooks)
- Alert severity levels and grouping
- Grafana dashboards
- AlertManager integration
- Web-based alert dashboard

## Quick Start

### Build
```bash
./build.sh
```

### Start All Services
```bash
./start.sh
```

### Stop All Services
```bash
./stop.sh
```

## Access Points
- Application: http://localhost:8084
- Alert Dashboard: http://localhost:8084/index.html
- Prometheus: http://localhost:9090
- AlertManager: http://localhost:9093
- Grafana: http://localhost:3000 (admin/admin)

## Testing Alerts
The system automatically simulates task failures to trigger alerts. Watch the dashboard for real-time alert notifications.

## Configuration
Edit `src/main/resources/application.yml` to configure:
- Alert thresholds
- Slack webhook URL
- Email settings
- Webhook endpoints
