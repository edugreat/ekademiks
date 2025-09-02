# Dockerfile (dev mode)
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy Maven wrapper and pom.xml first (dependency cache layer)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (online)
RUN ./mvnw dependency:go-offline -B

# Copy full source code
COPY src ./src

# Start Spring Boot in dev mode (hot reload enabled)
CMD ["./mvnw", "spring-boot:run"]
