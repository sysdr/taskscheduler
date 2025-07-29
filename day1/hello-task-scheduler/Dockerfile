# Multi-stage build for efficient containerization
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (optional for this demo)
EXPOSE 8080

# Add labels for documentation
LABEL maintainer="Task Scheduler Course"
LABEL description="Day 1: Hello Task Scheduler Demo"
LABEL version="1.0.0"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
