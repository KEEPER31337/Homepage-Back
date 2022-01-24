#!/bin/bash

cp /application.properties /home/keeper/src/main/resources/application.properties

cd /home/keeper

./gradlew bootJar
cp build/libs/*.jar /app.jar
java -jar /app.jar