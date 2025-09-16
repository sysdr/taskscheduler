FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/spring-cloud-coordination-*.jar app.jar
COPY ui/ ui/

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
