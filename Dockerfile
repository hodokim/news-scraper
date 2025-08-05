# 1. Java 17 버전을 포함한 베이스 이미지 설정
FROM eclipse-temurin:17-jdk-jammy

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Maven 빌드에 필요한 파일들을 먼저 복사하여 캐싱 활용
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# 4. Maven 의존성 다운로드
RUN ./mvnw dependency:go-offline

# 5. 전체 소스 코드 복사
COPY src ./src

# 6. Maven을 사용하여 애플리케이션 빌드 (테스트는 생략하여 빌드 속도 향상)
RUN ./mvnw clean package -DskipTests

# 7. 애플리케이션 실행
# pom.xml의 version에 맞춰 파일명을 확인
ENTRYPOINT ["java", "-jar", "target/news-scrap-0.0.1-SNAPSHOT.jar"]