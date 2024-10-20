FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /app
COPY ./pom.xml ./
COPY ./src ./src
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/sensor-monitoring-0.0.1-SNAPSHOT.jar app.jar

ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=sensor_db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=password
ENV SERVER_PORT=8082
ENV INPUT_PATH=/sensors/data
ENV OUTPUT_PATH=/sensors/view
ENV BATCH_SIZE=25000
ENV BATCH_FREQUENCY=2
ENV DEVIATION=0.2
ENV TZ=Europe/Tel_Aviv

ENTRYPOINT [ "java", "-jar", "app.jar" ]