FROM openjdk:21-jdk-slim

LABEL maintainer="task-scheduler-team"
LABEL description="Task Scheduler with Error Handling - Day 23"

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:resolve

# Copy source code
COPY src/ src/

# Build application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "target/task-scheduler-error-handling-1.0.0.jar"]
