FROM gradle:8.7.0-jdk21 AS builder

WORKDIR .

COPY . .

RUN gradle installDist

CMD ./build/install/app/bin/app