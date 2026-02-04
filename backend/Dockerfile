FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /editor
COPY pom.xml /editor/
RUN mvn dependency:go-offline

COPY src /editor/src/
RUN mvn clean install -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Install Docker CLI for Docker‑in‑Docker access
RUN apt-get update && apt-get install -y docker.io

WORKDIR /editor

# <-- fixed path here:
COPY --from=build /editor/target/editor-0.0.1-SNAPSHOT.jar /editor/editor.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/editor/editor.jar", "-Dspring.devtools.restart.enabled=true"]
