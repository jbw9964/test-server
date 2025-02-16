FROM openjdk:17

# dockerFile 의 work directory 설정
WORKDIR /web-application

# gradlew build 로 만들어진 `*.jar` 파일을 `/web-application/app.jar` 로 복사
# COPY 명령어 특성상 `*.jar` 파일 없으면 이미지 생성 실패
COPY ./build/libs/*.jar app.jar

# `openjdk:17` 이미지에 java 명령어 들어있으니까 `app.jar` 실행
ENTRYPOINT ["java", "-jar", "app.jar"]