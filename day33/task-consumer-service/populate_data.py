#!/usr/bin/env python3
"""
Script to populate Task Consumer Service with sample data
"""
import pika
import json
import time
from datetime import datetime

# RabbitMQ connection settings
RABBITMQ_HOST = "localhost"
RABBITMQ_PORT = 5672
RABBITMQ_USER = "guest"
RABBITMQ_PASS = "guest"
QUEUE_NAME = "task-execution-queue"

def send_task(channel, task_id, task_type, payload):
    """Send a task to RabbitMQ"""
    message = {
        "taskId": task_id,
        "taskType": task_type,
        "payload": payload,
        "scheduledTime": datetime.utcnow().isoformat() + "Z"
    }
    
    channel.basic_publish(
        exchange='',
        routing_key=QUEUE_NAME,
        body=json.dumps(message),
        properties=pika.BasicProperties(
            delivery_mode=2,  # Make message persistent
        )
    )
    print(f"   ✅ Sent: {task_id} ({task_type})")

def main():
    print("📦 Populating Task Consumer Service with sample data...")
    print("=" * 50)
    
    # Connect to RabbitMQ
    try:
        credentials = pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASS)
        parameters = pika.ConnectionParameters(
            host=RABBITMQ_HOST,
            port=RABBITMQ_PORT,
            credentials=credentials
        )
        connection = pika.BlockingConnection(parameters)
        channel = connection.channel()
        
        # Queue should already exist, we'll just publish to it
        # No need to declare if it already exists with different properties
        
        print("✅ Connected to RabbitMQ\n")
        
        # Sample tasks
        tasks = [
            # Email tasks
            ("email-001", "email", '{"to":"user1@example.com","subject":"Welcome Email","body":"Welcome to our service!"}'),
            ("email-002", "email", '{"to":"admin@example.com","subject":"Daily Report","body":"Your daily report is ready"}'),
            ("email-003", "email", '{"to":"support@example.com","subject":"New Ticket","body":"A new support ticket has been created"}'),
            
            # Notification tasks
            ("notif-001", "notification", '{"userId":"user123","type":"alert","message":"System maintenance scheduled"}'),
            ("notif-002", "notification", '{"userId":"user456","type":"info","message":"Your order has been shipped"}'),
            ("notif-003", "notification", '{"userId":"user789","type":"reminder","message":"Payment due in 3 days"}'),
            
            # Report tasks
            ("report-001", "report", '{"type":"monthly","format":"pdf","period":"2024-01"}'),
            ("report-002", "report", '{"type":"weekly","format":"csv","period":"2024-W05"}'),
            ("report-003", "report", '{"type":"daily","format":"xlsx","period":"2024-01-15"}'),
            
            # Backup tasks
            ("backup-001", "backup", '{"database":"users","type":"full","destination":"s3://backups/users"}'),
            ("backup-002", "backup", '{"database":"orders","type":"incremental","destination":"s3://backups/orders"}'),
            ("backup-003", "backup", '{"database":"logs","type":"archive","destination":"s3://backups/logs"}'),
            
            # Cleanup tasks
            ("cleanup-001", "cleanup", '{"target":"temp_files","retentionDays":7}'),
            ("cleanup-002", "cleanup", '{"target":"old_logs","retentionDays":30}'),
            ("cleanup-003", "cleanup", '{"target":"cache","retentionDays":1}'),
            
            # Generic tasks
            ("generic-001", "generic", '{"action":"validate_data","dataset":"customer_records"}'),
            ("generic-002", "generic", '{"action":"update_index","collection":"products"}'),
            ("generic-003", "generic", '{"action":"sync_cache","region":"us-east-1"}'),
        ]
        
        print("1. 📧 Sending email tasks...")
        for task in tasks[:3]:
            send_task(channel, *task)
            time.sleep(0.3)
        
        print("\n2. 🔔 Sending notification tasks...")
        for task in tasks[3:6]:
            send_task(channel, *task)
            time.sleep(0.3)
        
        print("\n3. 📊 Sending report tasks...")
        for task in tasks[6:9]:
            send_task(channel, *task)
            time.sleep(0.3)
        
        print("\n4. 💾 Sending backup tasks...")
        for task in tasks[9:12]:
            send_task(channel, *task)
            time.sleep(0.3)
        
        print("\n5. 🧹 Sending cleanup tasks...")
        for task in tasks[12:15]:
            send_task(channel, *task)
            time.sleep(0.3)
        
        print("\n6. ⚙️  Sending generic tasks...")
        for task in tasks[15:]:
            send_task(channel, *task)
            time.sleep(0.3)
        
        connection.close()
        
        print(f"\n✅ Sent {len(tasks)} sample tasks!")
        print("\n⏳ Waiting 5 seconds for tasks to be processed...")
        time.sleep(5)
        
        # Show stats
        print("\n📊 Current stats:")
        try:
            import urllib.request
            import urllib.parse
            response = urllib.request.urlopen("http://localhost:8080/api/stats")
            data = json.loads(response.read().decode())
            print(json.dumps(data, indent=2))
        except Exception as e:
            print(f"⚠️  Could not fetch stats: {e}")
        
        print("\n🎉 Data population complete!")
        print("💡 View the dashboard at: http://localhost:8080/")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())

