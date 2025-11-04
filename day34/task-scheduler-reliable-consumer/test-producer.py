#!/usr/bin/env python3
"""
Test producer to send tasks to Kafka for testing the consumer
"""

import json
import time
import random
from kafka import KafkaProducer
from datetime import datetime, timedelta

def create_task(task_id, task_type, **kwargs):
    return {
        "id": task_id,
        "type": task_type,
        "payload": kwargs,
        "priority": random.randint(1, 5),
        "createdAt": datetime.now().isoformat(),
        "scheduledAt": datetime.now().isoformat(),
        "retryCount": 0,
        "maxRetries": 3
    }

def main():
    producer = KafkaProducer(
        bootstrap_servers=['localhost:9092'],
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        key_serializer=lambda k: k.encode('utf-8') if k else None
    )

    print("🚀 Starting task producer...")
    
    task_types = [
        ("EMAIL", {"recipient": "user@example.com", "subject": "Test Email"}),
        ("EMAIL", {"recipient": "admin@company.com", "subject": "System Alert"}),
        ("REPORT", {"reportType": "sales", "dateRange": "2024-01-01 to 2024-01-31"}),
        ("REPORT", {"reportType": "inventory", "dateRange": "last_week"}),
        ("EMAIL", {"recipient": "", "subject": "Invalid Email"}),  # Will cause permanent failure
        ("UNKNOWN", {"data": "test"}),  # Will cause permanent failure
    ]

    try:
        for i in range(50):
            task_type, payload = random.choice(task_types)
            task = create_task(f"task-{i+1:03d}", task_type, **payload)
            
            producer.send('task-execution', key=task['id'], value=task)
            print(f"📤 Sent task {task['id']} of type {task['type']}")
            
            # Random delay between 0.5 and 2 seconds
            time.sleep(random.uniform(0.5, 2.0))
            
    except KeyboardInterrupt:
        print("\n⏹️ Stopping producer...")
    finally:
        producer.close()
        print("✅ Producer closed")

if __name__ == "__main__":
    main()
