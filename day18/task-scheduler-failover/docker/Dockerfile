FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/task-scheduler-failover-1.0.0.jar app.jar
COPY web/ web/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
