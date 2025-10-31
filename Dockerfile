FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

# Собираем JAR файл вместо installDist
RUN ./gradlew clean build

# Второй этап для минимального образа
FROM openjdk:21-jre-slim

WORKDIR /app

# Копируем собранный JAR из первого этапа
COPY --from=builder /app/build/libs/*.jar app.jar

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]