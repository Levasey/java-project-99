# Use Gradle with JDK 21
FROM gradle:8.12.1-jdk21 AS builder

WORKDIR /build

# Copy everything except what's in .dockerignore
COPY . .

# Build the application
RUN ./gradlew clean build

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]