FROM openjdk:17-slim

RUN apt-get update

WORKDIR /app

COPY ./gradlew .
COPY ./gradle ./gradle
COPY ./build.gradle .
COPY ./settings.gradle .
COPY ./src ./src

# 종속성 미리 다운
RUN ./gradlew --no-daemon build -x test

EXPOSE 8080
COPY ./build/libs/maumRecord-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
#아래는 배포용 코드
#CMD ["./gradlew", "--no-daemon", "bootRun"]