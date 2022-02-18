FROM gradle:7.3.2-jdk17

WORKDIR /home/keeper

COPY ../config/application.properties /application.properties
COPY ../config/entrypoint.sh /entrypoint.sh