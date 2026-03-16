## Multi-stage Dockerfile for Spring Boot app on Render

# ====== Build stage ======
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom and wrapper first for better layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Pre-fetch dependencies (optional but speeds up subsequent builds)
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline -DskipTests

# Copy the rest of the source code
COPY src src
COPY deploy deploy
COPY run.sh run.sh

# Build the application jar
RUN ./mvnw clean package -DskipTests

# ====== Runtime stage ======
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/campus-food-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Render will map to $PORT
EXPOSE 8080

# Default profile: dev (H2) unless overridden by SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080} --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

