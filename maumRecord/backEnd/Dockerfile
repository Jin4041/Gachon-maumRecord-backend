FROM openjdk:17-slim

RUN apt-get update

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# gradlew 실행 권한 부여
RUN chmod +x gradlew

EXPOSE 8080
#배포용
#CMD ["java", "-jar", "app.jar"]
CMD ["./gradlew", "--no-daemon", "bootRun"]