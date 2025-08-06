FROM openjdk:21
COPY build/libs/content_service-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]