FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
RUN mkdir -p logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
