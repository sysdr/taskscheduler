FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/thread-pool-scheduler-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
