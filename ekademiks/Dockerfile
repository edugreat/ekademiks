# Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy only the files needed for dependency resolution first (for better caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies first (this helps with caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application WITHOUT offline flag
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]