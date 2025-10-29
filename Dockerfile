FROM gradle:8.14.3-jdk21

WORKDIR /build

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build

CMD ["./gradlew", "run"]