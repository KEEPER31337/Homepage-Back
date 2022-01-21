FROM openjdk:17

WORKDIR /home/keeper

COPY application.properties /application.properties
COPY ./entrypoint.sh /entrypoint.sh