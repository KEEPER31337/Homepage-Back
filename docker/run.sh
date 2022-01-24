#!/bin/sh

docker build -t keeper:0.1 -f app.dockerfile .

docker-compose -p keeper up