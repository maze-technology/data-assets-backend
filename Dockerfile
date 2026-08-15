# Spring Boot fat-jar runtime image (avoids Paketo downloading Liberica from GitHub in CI).
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
# Prefer non-SNAPSHOT when tagged builds produce versioned jars
COPY build/libs/*.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
