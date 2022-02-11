FROM openjdk:17

COPY ../app.jar /app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]