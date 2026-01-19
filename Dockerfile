# Multi-stage build for Spring Boot app
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests && \
    echo "=== Build complete ===" && \
    ls -la target/ && \
    echo "=== JAR files in target ===" && \
    find target -name "*.jar" -type f

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the executable JAR (not the .original)
COPY --from=build /app/target/ai-news-app-1.0.0.jar app.jar

# Expose port (Railway will set PORT env var)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
