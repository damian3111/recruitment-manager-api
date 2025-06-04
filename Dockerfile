#FROM eclipse-temurin:17-jdk-alpine
#
#WORKDIR /app
#
#COPY target/recruitment-manager-api-0.0.1-SNAPSHOT.jar app.jar
#
#EXPOSE 8080
#
#ENV JAVA_OPTS=""
#
## Run the application
#ENTRYPOINT exec java $JAVA_OPTS -jar app.jar


# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/recruitment-manager-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]