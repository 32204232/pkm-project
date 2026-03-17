# backend/Dockerfile

# 1. 빌드 단계 (JDK 17 환경에서 애플리케이션을 빌드합니다)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# 권한 부여 및 빌드 실행 (테스트는 생략하여 빌드 속도 향상)
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

# 2. 실행 단계 (가벼운 JRE 환경만 사용하여 이미지를 최적화합니다)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 빌드 단계에서 만들어진 jar 파일을 가져옵니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 8080 포트 개방 및 스프링 부트 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]