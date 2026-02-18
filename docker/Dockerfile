FROM openjdk:17-jre-slim

WORKDIR /app

COPY oj-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]