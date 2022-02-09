docker build -t keeper-homepage-app:test -f dockerfile/app.dockerfile .
docker build --platform=linux/amd64 -t keeper-homepage-db:test -f dockerfile/db.dockerfile .

docker-compose -p keeper up