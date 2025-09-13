# Multi-stage Docker build for User Service
# Stage 1: Build stage
FROM maven:3.9.5-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy parent POM and properties
COPY pom.xml pom.properties ./

# Copy common module
COPY modules/common ./modules/common

# Copy user-service module
COPY modules/user-service ./modules/user-service

# Build the application
RUN mvn clean package -pl modules/user-service -am -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/modules/user-service/target/user-service-*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=docker"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="Synchrony Team <team@synchrony.com>"
LABEL version="1.0.0"
LABEL description="User Service - User management and image handling microservice"
LABEL org.opencontainers.image.source="https://github.com/synchrony/image-management-platform"
LABEL org.opencontainers.image.documentation="https://github.com/synchrony/image-management-platform/blob/main/README.md"