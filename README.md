
# 홈 서버 CD 테스트용 repo

---

## Docker

- `test-server.dockerfile` : 우리 서버를 dockerize 할 명령어들의 집합

```dockerfile
FROM openjdk:17

# dockerFile 의 work directory 설정
WORKDIR /web-application

# gradlew build 로 만들어진 `*.jar` 파일을 `/web-application/app.jar` 로 복사
# COPY 명령어 특성상 `*.jar` 파일 없으면 이미지 생성 실패
COPY ./build/libs/*.jar app.jar

# `openjdk:17` 이미지에 java 명령어 들어있으니까 `app.jar` 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
```

> `CMD` vs `ENTRYPOINT` ? 
> - `CMD` : 이미지를 통해 컨테이너 생성 시, **실행할 default 명령어**
> - `ENTRYPOINT` : 이미지를 통해 컨테이너 생성 시, **반드시 실행할 명령어**


- `dockerfile` 을 통해 서버 docker image 를 생성 (dockerize)

```bash
$ docker build -t [이미지 이름]:[태그] -f [dockerFile 이름] [dockerFile 을 찾을 경로]
```

- 생성한 이미지를 docker hub 에 upload

```bash
# private repo 에 push 하기 위해 로그인. 둘중 아무거나
$ docker -u [유저 이름] -p [비밀번호]
$ echo [비밀번호] | docker login -u [유저 이름] --password-stdin

# 이미지 push 하려면 이미지 이름을 [유저 이름]/[repo 이름]:[tag] 형식으로 바꿔줘야 함
# `tag` 또는 `image tag` 명령어로 새로운 image 를 만듬
# 공식 문서에도 이 둘은 차이 없다고 함 (alias)
$ docker tag [이미지 이름]:[tag] [유저 이름]/[repo 이름]:[tag]
$ docker image tag [이미지 이름]:[tag] [유저 이름]/[repo 이름]:[tag]

# [유저 이름]/[repo 이름]:[tag] (이름 바꿔 만든 image 이름 그대로) 이미지를 push
$ docker push [유저 이름]/[repo 이름]:[tag]
```

- docker hub 에 올라간 이미지를 pull

```bash
$ docker pull [유저 이름]/[repo 이름]:[tag]
```

- container 실행

```bash
$ docker run -d -p 8080:8080 \ 
> -e SPRING_PROFILES_ACTIVE=local-docker \ 
> --name [container 이름] [이미지 이름]:[tag]
```

이 때 `-e SPRING_PROFILES_ACTIVE=...` 로 실행할 spring profile 을 제공

`-d` 옵션은 컨테이너 background 로 돌리고 container ID 출력하는 옵션

`:[tag]` 생략시 `:latest` 붙은 이미지를 찾아 돌림.

- container, image 정지 or 삭제

```bash
# container 정지
$ docker stop [컨테이너 이름]

# 사용하지 않는 container 들 삭제
$ docker container prune -f

# 사용하지 않는 / container 로 사용하지 않는 image 들 삭제
$ docker image prune -af
```

`-f` 옵션은 `삭제 Y/N` 안 묻고 삭제하는 옵션

---

## GitHub Action



---

## Spring Profiles

Profile 을 3 개로 분리. `database`, `monitoring`, `[실행 위치]`

`[실행 위치]` 는 `local`, `local-docker`, `prod-test` 등 `실배포`/`테스트 배포`/`개발` 에 따라 변경하느 profile.

그래서 `[실행 위치]` profile 에는 `database`, `monitoring` 에 필요한 값들을 설정하는 모습으로 만듬.

```properties
# application-database.properties
spring.datasource.url=jdbc:mysql://${db-host:localhost}:3306/${db-name}
spring.datasource.username=${db-username}
spring.datasource.password=${db-password}
spring.jpa.properties.hibernate.dialect=${db-dialect}
spring.jpa.hibernate.ddl-auto=${db-ddl}
```
```properties
# application-local.properties
db-host=localhost
db-name=local-test-db
db-username=root
db-password=root
db-dialect=org.hibernate.dialect.MySQLDialect
db-ddl=update
```

`local` profile 이 `db-host`, `db-username` 같은걸 설정해 `database` profile 에 사용하는 것을 볼 수 있음.

`monitoring` 은 환경별 설정할게 없어서 생략.

기본 `application.properties` 는 아래처럼 구성

```properties
# application.properties
spring.application.name=test-server
management.server.port=8080

spring.profiles.group.essential=database,monitoring
spring.profiles.include=essential

spring.profiles.active=local
```

위 `spring.profiles.active=local` 이 있는데, 이는 `docker run ...` 시 `-e SPRING_PROFILES_ACTIVE=...` 명령어로 **overload** 할 수 있음.

`-e SPRING_PROFILES_ACTIVE=local-docker` 로 하면 아래처럼 돌리는 것과 동일.

```properties
spring.application.name=test-server
management.server.port=8080

spring.profiles.group.essential=database,monitoring
spring.profiles.include=essential

spring.profiles.active=local-docker
```

---

