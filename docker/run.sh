#!/bin/sh

docker-compose -p keeper up -d

FILE="/home/keeper/src/main/resources/application.properties"
if [ ! -e $FILE ]; then
    docker cp application.properties app:/home/keeper/src/main/resources
fi

docker exec -w /home/keeper -it app sh -c "chmod +x gradlew &&
                                           ./gradlew bootJar &&
                                           cp build/libs/*.jar /app.jar &&
                                           java -jar /app.jar"

docker stop app db