# -------- Stage 1 : Build the application ------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /workspace

# Copy only pom files first to leverage Docker cache
COPY pom.xml pom.properties ./
COPY modules/common/pom.xml modules/common/pom.xml
COPY modules/user-service/pom.xml modules/user-service/pom.xml

# Go offline to resolve deps
RUN mvn -B -q dependency:go-offline

# Copy the rest of the source and build
COPY . .
RUN mvn -B clean package -pl modules/user-service -am -DskipTests

# -------- Stage 2 : Slim runtime image ----------------------
FROM eclipse-temurin:17-jre

ENV APP_HOME=/app
WORKDIR ${APP_HOME}

# Copy built JAR from builder stage
COPY --from=builder /workspace/modules/user-service/target/user-service-*.jar app.jar

# Expose HTTP port
EXPOSE 8080

# Healthcheck (Spring Boot actuator)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM tuning suitable for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
