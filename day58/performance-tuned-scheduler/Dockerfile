FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar

# Enable JMX for Docker
ENV JAVA_OPTS="-Dcom.sun.management.jmxremote \
               -Dcom.sun.management.jmxremote.port=9010 \
               -Dcom.sun.management.jmxremote.rmi.port=9010 \
               -Dcom.sun.management.jmxremote.authenticate=false \
               -Dcom.sun.management.jmxremote.ssl=false \
               -Djava.rmi.server.hostname=localhost"

EXPOSE 8058 9010
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
