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
    find target -name "*.jar" -type f && \
    echo "=== Finding executable JAR (excluding .original) ===" && \
    find target -name "*.jar" -not -name "*.original" -type f

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy all non-.original JAR files and rename to app.jar
# Using a shell to handle the copy with exclusion
COPY --from=build /app/target/*.jar ./

# Keep only the non-.original file and rename it
RUN rm -f *.original && \
    mv *.jar app.jar && \
    ls -lh app.jar && \
    echo "JAR file ready: app.jar"

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
