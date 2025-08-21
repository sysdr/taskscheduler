FROM openjdk:21-jdk-slim

LABEL maintainer="Task Scheduler Team"
LABEL description="Ultra-Scalable Task Scheduler API"

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Build application
RUN mvn clean package -DskipTests

# Copy the jar file
RUN cp target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/tasks/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
