#!/bin/bash
echo "🚀 Starting Performance Tuned Scheduler..."

# Check if JAR exists
if [ ! -f target/*.jar ]; then
    echo "📦 Building project first..."
    ./build.sh
fi

echo "🌐 Dashboard will be available at http://localhost:8058"
echo "📊 JMX port for JVisualVM: 9010"
echo ""
echo "To connect JVisualVM:"
echo "1. Run: jvisualvm"
echo "2. Look for 'performance-tuned-scheduler' under Local"
echo "3. Or add JMX connection: localhost:9010"
echo ""

java -Dcom.sun.management.jmxremote \
     -Dcom.sun.management.jmxremote.port=9010 \
     -Dcom.sun.management.jmxremote.rmi.port=9010 \
     -Dcom.sun.management.jmxremote.authenticate=false \
     -Dcom.sun.management.jmxremote.ssl=false \
     -Djava.rmi.server.hostname=localhost \
     -jar target/*.jar
