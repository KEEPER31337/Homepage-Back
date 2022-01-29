docker build -t keeper:0.1 -f dockerfile/app.dockerfile .
docker build --platform=linux/amd64 -t keeper_db:0.1 -f dockerfile/db.dockerfile .

docker-compose -p keeper up