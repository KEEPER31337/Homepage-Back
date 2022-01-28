#!/bin/sh

docker build -t keeper:0.2 -f app.dockerfile .

docker-compose -p keeper up