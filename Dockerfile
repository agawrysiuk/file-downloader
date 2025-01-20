FROM openjdk:17-jdk-slim
VOLUME ["/data"]
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
