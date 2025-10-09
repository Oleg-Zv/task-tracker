FROM gradle:jdk17 AS build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew clean build -x test --no-daemon


FROM openjdk:17-slim
WORKDIR /app

ARG jar_file=build/libs/*.jar
COPY --from=build /app/${jar_file} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
