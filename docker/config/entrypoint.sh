#!/bin/bash

cp /application.properties /home/keeper/src/main/resources/application.properties

cd /home/keeper

gradle bootJar
cp build/libs/homepage-0.0.1-SNAPSHOT.jar /app.jar
java -jar /app.jar