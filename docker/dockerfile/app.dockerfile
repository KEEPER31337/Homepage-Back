FROM gradle:7.3.2-jdk17

COPY ../app.jar /app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]