# Docker for Keeper Homepage Backend

## 설명

- docker-compose.yml
  - GitHub Actions CICD용 파일
- local
  - 백엔드 로컬 개발 환경을 위한 디렉터리

## 로컬 환경 개발 구축

- 환경 구축을 위해 [docker](https://www.docker.com/products/docker-desktop/) 프로그램이 필요합니다.
- MySQL(3306), redis(6379) 컨테이너를 생성합니다.
- **config 디렉터리 안에 DB를 초기화 할 init.sql 파일이 존재해야합니다.**
- **docker의 컨테이너와 통신하기 위해 Spring Boot의 application.properties 내용을 수정해야 합니다.**

```yaml
# application.properties

spring.datasource.url=jdbc:mysql://db:3306/keeper?autoReconnect=true&serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=utf8
spring.datasource.username=keeper
spring.datasource.password=keeper

spring.redis.host=redis
spring.redis.port=6379
```

```bash
# 환경 구축

cd local
./localStart.sh
```
