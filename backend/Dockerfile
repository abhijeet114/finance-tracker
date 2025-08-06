# Build stage
FROM amazoncorretto:21-alpine-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean generate-sources compile test package

# Runtime stage
FROM amazoncorretto:21-alpine

WORKDIR /app

# Install curl for health check
RUN apk add --no-cache curl

# Create app user
RUN addgroup -g 1001 -S app && \
    adduser -u 1001 -S app -G app

# Copy the jar file from build stage
COPY --from=build /app/target/*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown app:app /app/logs

# Change to app user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
