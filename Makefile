.DEFAULT_GOAL := build-run

coverage:
	./gradlew jacocoTestReport
	open build/reports/jacoco/test/html/index.html

setup:
	./gradlew wrapper --gradle-version 8.13

clean:
	./gradlew clean

build:
	./gradlew build

install:
	./gradlew install

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew test jacocoTestReport

lint:
	./gradlew checkstyleMain

update-deps:
	./gradlew refreshVersions
	# ./gradlew dependencyUpdates -Drevision=release

build-run: build run

.PHONY: build run build-run clean install test report lint update-deps setup