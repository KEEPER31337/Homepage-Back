#!/bin/bash

docker build -t keeper-homepage-db:local -f db.dockerfile .

docker-compose -f docker-compose.local.yml up -d
