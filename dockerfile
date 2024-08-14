# Stage 1: Base image with Gradle
FROM gradle:8.8-jdk17 AS gradle-base

# Set work directory
WORKDIR /home/gradle/project

# Copy the Gradle wrapper and properties
COPY gradle /home/gradle/project/gradle
COPY gradlew /home/gradle/project/gradlew

# Download dependencies (cached if no changes in build files)
RUN ./gradlew --no-daemon build || return 0

# Stage 2: Build the application
FROM gradle-base AS build
COPY . /home/gradle/project/
RUN ./gradlew clean bootJar

# Stage 3: Create a minimal image for running the application
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar /app/app.jar

# Copy Liquibase changelog files to the final image
COPY --from=build /home/gradle/project/src/main/resources/db/changelog /app/db/changelog

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]