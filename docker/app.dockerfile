FROM gradle:7.3.2-jdk17

WORKDIR /home/keeper

COPY application.properties /application.properties
COPY ./entrypoint.sh /entrypoint.sh