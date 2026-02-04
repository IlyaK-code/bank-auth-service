FROM eclipse-temurin:17-jdk-alpine

COPY target/authorization-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "/app.jar"]