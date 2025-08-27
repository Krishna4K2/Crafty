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

# Copy the built jar from the builder stage
COPY --from=builder /app/target/voting-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8086


# Always use H2 profile for standalone Docker runs
ENV SPRING_PROFILES_ACTIVE=h2

CMD ["java", "-jar", "app.jar"]