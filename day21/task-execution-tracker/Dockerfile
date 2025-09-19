FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/task-execution-tracker-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
