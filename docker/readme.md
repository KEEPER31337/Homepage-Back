# Docker를 통한 Keeper Homepage Backend 환경 구성
## 설명
- Spring Boot, Mysql, redis 컨테이너 생성
- 첫 실행은 Spring Boot의 빌드와 의존성 설치로 약 3분 소요
- Mysql의 DB 초기화 진행
- API 문서를 최신화 하고 싶다면 develop 브랜치에서 ```git pull``` 수행 후 ```run.sh/bat``` 실행
- ```run.sh/bat``` 스크립트를 CTRL+C로 실행 취소 및 컨테이너 stop 동작

## 실행
Keeper Homepage Backend 환경 구성을 위해 ```docker/.env``` 파일과 ```docker/config/init.sql``` 2개의 설정 파일 필요합니다.
### Linux/Mac
```bash
git clone https://github.com/KEEPER31337/Homepage-Back.git

cd Homepage-Back/docker

./run.sh
```

### Windows
```bash
git clone https://github.com/KEEPER31337/Homepage-Back.git

cd Homepage-Back/docker

run.bat # 혹은 파일 클릭
```

### API 문서 최신화
```bash
git pull # develop branch

cd Homepage-Back/docker

./run.sh # 혹은 run.bat
```

[localhost:8080/docs/keeper.html](http://localhost:8080/docs/keeper.html) 접속