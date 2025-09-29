FROM amazoncorretto:17-jdk AS build
WORKDIR /app

COPY build.gradle settings.gradle gradlew gradlew.bat gradle/ ./
RUN ./gradlew dependencies --no-daemon

RUN chmod +x ./gradlew
COPY src ./src
RUN ./gradlew clean build -x test --no-deamon


FROM amazoncorretto:17-jre
WORKDIR /app

ARG jar_file=build/libs/*.jar
COPY --from=build /app/${jar_file} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]