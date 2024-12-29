FROM eclipse-temurin:23-jdk AS builder

LABEL maintainer="huiqing"

## How to build the application

WORKDIR /app

# copy files
COPY ./mvnw .
COPY .mvn .mvn

COPY pom.xml .
COPY src src

# make mvnw executable
RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true
# /app/target/eventmanagement-0.0.1-SNAPSHOT.jar

FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /app/target/mini_project_1-0.0.1-SNAPSHOT.jar vttpb-mini_project_1.jar

# check if curl command is available
RUN apt update && apt install -y curl


ENV PORT=8080
ENV SPRING_DATA_REDIS_HOST=localhost SPRING_DATA_REDIS_PORT=6379
ENV SPRING_DATA_REDIS_USERNAME="" SPRING_DATA_REDIS_PASSWORD=""

EXPOSE ${PORT}

SHELL [ "/bin/sh", "-c" ]

ENTRYPOINT SERVER_PORT=${PORT} java -jar vttpb-mini_project_1.jar
