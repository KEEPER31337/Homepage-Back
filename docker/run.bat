IF EXIST ESSENTIAL (
    docker rmi -f keeper-homepage-app:test keeper-homepage-db:test

    robocopy ESSENTIAL . /E /IS

    ren env .env

    docker build -t keeper-homepage-app:test -f dockerfile/app.dockerfile .
    docker build --platform=linux/amd64 -t keeper-homepage-db:test -f dockerfile/db.dockerfile .
)

docker-compose -p keeper up