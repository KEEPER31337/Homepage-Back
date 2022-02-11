#!/bin/bash

if [ ! -f ESSENTIAL ]; then
    docker rmi -f keeper-homepage-app:test keeper-homepage-db:test

    cp -af ESSENTIAL/* .

    mv env .env

    docker build -t keeper-homepage-app:test -f dockerfile/app.dockerfile .
    docker build --platform=linux/amd64 -t keeper-homepage-db:test -f dockerfile/db.dockerfile .
fi

docker-compose -p keeper up