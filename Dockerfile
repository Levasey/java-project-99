FROM gradle:8.14.3-jdk-21

WORKDIR /app

# Копируем исходный код
COPY . .

# Даем права на выполнение gradlew (если есть)
RUN chmod +x ./gradlew

# Собираем приложение
RUN ./gradlew installDist

# Запускаем приложение
CMD ["./build/install/app/bin/app"]