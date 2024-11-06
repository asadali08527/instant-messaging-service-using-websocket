# Stage 1 - Build the application
FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2 - Create the runtime image
# Use an official Eclipse Temurin (OpenJDK) image for Java 17
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built application from the first stage
COPY --from=build /app/target/chatservice-1.0.0-SNAPSHOT-runner.jar /app/app.jar

# Expose the WebSocket or API ports (adjust as necessary)
EXPOSE 8080

# Set environment variables if required (these will be overwritten by AWS envs)
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0"

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
