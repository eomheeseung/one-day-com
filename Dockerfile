# 베이스 이미지로 OpenJDK 17 사용
FROM openjdk:17-jdk-slim

# 작업 디렉터리 설정
WORKDIR /app

# 빌드된 Spring Boot JAR 파일을 복사
COPY build/libs/in-continue-dev-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 포트 공개
EXPOSE 8080
