# Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and source code
COPY . .

RUN chmod +x mvnw
# Build the application (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built jar from the builder stage
COPY --from=builder /app/target/voting-0.0.1-SNAPSHOT.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8086

# Remove the hardcoded healthcheck since docker-compose handles it
# HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
#   CMD curl -f http://localhost:8086/actuator/health || exit 1

# Remove hardcoded profile - let docker-compose control it
# ENV SPRING_PROFILES_ACTIVE=h2

CMD ["java", "-jar", "app.jar"]