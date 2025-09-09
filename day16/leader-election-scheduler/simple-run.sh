#!/bin/bash

echo "🚀 Starting Leader Election Scheduler..."

# Set Java classpath for Spring Boot
export CLASSPATH="src/main/java:src/main/resources"

# Download Spring Boot dependencies if not present
if [ ! -d "lib" ]; then
    echo "📦 Downloading dependencies..."
    mkdir -p lib
    
    # Download Spring Boot JARs (simplified approach)
    curl -s -o lib/spring-boot-starter-web.jar "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-web/3.2.0/spring-boot-starter-web-3.2.0.jar" || echo "Failed to download spring-boot-starter-web"
    curl -s -o lib/spring-boot-starter-data-jpa.jar "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa/3.2.0/spring-boot-starter-data-jpa-3.2.0.jar" || echo "Failed to download spring-boot-starter-data-jpa"
    curl -s -o lib/h2.jar "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar" || echo "Failed to download h2"
fi

# Compile Java files
echo "🔨 Compiling Java files..."
find src/main/java -name "*.java" -exec javac -cp "lib/*" {} \;

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🚀 Starting application..."
    java -cp "src/main/java:lib/*" com.scheduler.LeaderElectionSchedulerApplication
else
    echo "❌ Compilation failed!"
    exit 1
fi
