FROM openjdk:21-jdk-slim

LABEL maintainer="Ultra-Scale Task Scheduler Demo"
LABEL version="1.0.0"
LABEL description="Demonstrates @Scheduled limitations in distributed environments"

WORKDIR /app

# Copy the built JAR file
COPY target/scheduled-duplication-demo-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
