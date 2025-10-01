FROM  amazoncorretto:17 AS build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew clean build -x test --no-daemon


FROM amazoncorretto:17-alpine
WORKDIR /app

ARG jar_file=build/libs/*.jar
COPY --from=build /app/${jar_file} app.jar

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]