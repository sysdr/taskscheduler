FROM openjdk:21-jre-slim

WORKDIR /app

COPY build/libs/redlock-task-scheduler-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
