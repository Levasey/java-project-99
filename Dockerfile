FROM gradle:8.14.3-jdk-21-and-24

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD ./build/install/app/bin/app