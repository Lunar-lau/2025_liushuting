# Dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean install -DskipTests

FROM openjdk:21-slim

WORKDIR /app

COPY --from=build /app/target/coin-combination-1.0-SNAPSHOT.jar ./coin-combination-1.0-SNAPSHOT.jar
COPY src/main/resources/config.yml ./config.yml

EXPOSE 8080
EXPOSE 8081

CMD ["java", "-jar", "coin-combination-1.0-SNAPSHOT.jar", "server", "config.yml"]
