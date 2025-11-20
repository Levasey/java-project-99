FROM gradle:8.7.0-jdk21 AS builder

WORKDIR . .

COPY . .

RUN chmod +x ./gradlew

# Собираем JAR файл
RUN ./gradlew clean build

# Второй этап для минимального образа
FROM openjdk:21-slim

WORKDIR /app

# Копируем собранный JAR из первого этапа
COPY --from=builder /app/build/libs/*.jar app.jar

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]