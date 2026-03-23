FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java","-jar","app.jar"]