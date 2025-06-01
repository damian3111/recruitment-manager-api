FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/recruitment-manager-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
