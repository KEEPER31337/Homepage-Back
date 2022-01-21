# API 문서 보기
## 설명
- docker, docker-compose를 통해 Spring Boot를 빌드하고 실행
- Spring Boot, Mysql 컨테이너 생성
- 첫 실행은 빌드와 의존성 설치로 약 3분 소요
- API 문서를 최신화 하고 싶다면 develop 브랜치에서 ```git pull``` 수행 후 ```run.sh/ps1``` 실행
- ```run.sh/ps1``` 스크립트를 CTRL+C로 실행 취소 및 컨테이너 stop 동작

## 실행
### Linux/Mac
```bash
git clone https://github.com/KEEPER31337/Homepage-Back.git

cd Homepage-Back/docker

./run.sh
```

### Windows
```
git clone https://github.com/KEEPER31337/Homepage-Back.git

cd Homepage-Back/docker

.\run.ps1
```

### API 문서 최신화
```bash
git pull # develop branch

cd Homepage-Back/docker

./run.sh # .\run.ps1
```

[localhost:8080/docs/keeper.html](http://localhost:8080/docs/keeper.html) 접속