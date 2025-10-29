FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/graceful-shutdown-scheduler-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
