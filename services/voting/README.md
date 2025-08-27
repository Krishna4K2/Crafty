# Crafty Voting Service - Complete Setup Guide

## Overview
The Voting Service is a Java Spring Boot microservice that manages origami voting functionality. It synchronizes origami data from the Catalogue Service and provides voting capabilities with a choice between H2 (in-memory) and MongoDB databases.

## Prerequisites
- **Java JDK 21** installed and `JAVA_HOME` set
- **Maven 3.9.11** or use the included Maven Wrapper (`mvnw`/`mvnw.cmd`)
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Local Environment Setup

### 1. Clone and Navigate
```sh
git clone <repository-url>
cd Crafty/services/voting
```

### 2. Java and Maven Setup
```sh
# Verify Java installation
java -version
# Should show: Java 21.x.x

# Verify Maven installation (or use Maven Wrapper)
mvn -version
# Or use Maven Wrapper (recommended)
./mvnw -version  # Linux/macOS/Git Bash
mvnw.cmd -version  # Windows CMD
```

### 3. Environment Variables (Optional)
The service uses default configurations but you can customize:

```sh
# Port configuration (default: 8086)
export SERVER_PORT=8086

# Catalogue service URL (default: http://localhost:5000/api/products)
export CATALOGUE_SERVICE_URL=http://localhost:5000/api/products

# Database configuration (for MongoDB support)
export MONGODB_URI=mongodb://localhost:27017/voting
export MONGODB_DATABASE=voting
```

## How to Build & Run the Voting Service Locally

### Build & Test Commands
Run these commands from the `services/voting` directory:

**Build the app (skip tests):**
```sh
mvn clean package -DskipTests
# Or use Maven Wrapper:
./mvnw clean package -DskipTests   # (Linux/macOS/Git Bash)
mvnw.cmd clean package -DskipTests # (Windows CMD)
```

**Build the app (run tests):**
```sh
mvn clean package
# Or use Maven Wrapper:
./mvnw clean package
mvnw.cmd clean package
```

**Compile only (no packaging):**
```sh
mvn clean compile
# Or use Maven Wrapper:
./mvnw clean compile
mvnw.cmd clean compile
```

**Run tests only:**
```sh
mvn test
# Or use Maven Wrapper:
./mvnw test
mvnw.cmd test
```

**Clean build artifacts:**
```sh
mvn clean
# Or use Maven Wrapper:
./mvnw clean
mvnw.cmd clean
```

The built JAR will be located in the `target/` folder, e.g. `target/voting-0.0.1-SNAPSHOT.jar`.

### Run the Service (Locally)

To run the voting service locally using the default H2 in-memory database:

1. Build the JAR as described above.
2. Start the service:
  ```sh
  java -jar target/voting-0.0.1-SNAPSHOT.jar
  ```
3. The service will start on port **8086** by default.
4. Access the app in your browser at [http://localhost:8086](http://localhost:8086)

### Service Endpoints
- **GET** `/api/origamis` - Get all origamis
- **GET** `/api/origamis/{id}` - Get specific origami
- **GET** `/api/origamis/{id}/votes` - Get vote count
- **POST** `/api/origamis/{id}/vote` - Vote for an origami
- **POST** `/api/origamis` - Add new origami
- **GET** `/api/origamis/status` - Service status
- **GET** `/h2-console` - H2 database console (when using H2)

## Docker Setup

### Build & Run with Docker
You do NOT need to build the JAR locally. The Docker build will handle everything.

#### **Standalone Docker (always H2, in-memory)**
```sh
docker build -t voting-app .
docker run -p 8086:8086 voting-app
```
> The standalone Docker image always uses H2. No environment variable needed.

#### **Docker Compose (always MongoDB)**
Use Docker Compose to run both the voting service and MongoDB:
```sh
docker-compose up --build
```
> The voting service will always use MongoDB in Docker Compose. No environment variable needed.

### Individual Docker Container Setup

#### 1. Build the Docker Image
```sh
# From services/voting directory
docker build -t crafty-voting-service .
```

#### 2. Run Individual Container
```sh
# Basic run (H2 database)
docker run -d \
  --name voting-service \
  -p 8086:8086 \
  crafty-voting-service

# With custom environment variables
docker run -d \
  --name voting-service \
  -p 8086:8086 \
  -e SERVER_PORT=8086 \
  -e CATALOGUE_SERVICE_URL=http://host.docker.internal:5000/api/products \
  crafty-voting-service
```

#### 3. Run with MongoDB Support
```sh
# First, start MongoDB
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_DATABASE=voting \
  mongo:latest

# Then start voting service with MongoDB
docker run -d \
  --name voting-service \
  -p 8086:8086 \
  --link mongodb:mongodb \
  -e SPRING_PROFILES_ACTIVE=mongodb \
  -e MONGODB_URI=mongodb://mongodb:27017/voting \
  crafty-voting-service
```

### Docker Networking - Connecting to Other Services

#### Create a Common Docker Network
```sh
# Create a custom network for all services
docker network create crafty-network
```

#### Run Services on the Same Network
```sh
# 1. Start Catalogue Service
docker run -d \
  --name catalogue-service \
  --network crafty-network \
  -p 5000:5000 \
  crafty-catalogue-service

# 2. Start Voting Service (connected to catalogue)
docker run -d \
  --name voting-service \
  --network crafty-network \
  -p 8086:8086 \
  -e CATALOGUE_SERVICE_URL=http://catalogue-service:5000/api/products \
  crafty-voting-service

# 3. Start Frontend Service (connected to both)
docker run -d \
  --name frontend-service \
  --network crafty-network \
  -p 3000:3000 \
  -e CATALOGUE_BASE_URI=http://catalogue-service:5000 \
  -e VOTING_BASE_URI=http://voting-service:8086 \
  crafty-frontend-service
```

#### Inspect Network
```sh
# Check network details
docker network inspect crafty-network

# List containers on network
docker network ls
```

### Docker Compose for Multi-Service Setup

#### Using Docker Compose (Recommended)
```yaml
# docker-compose.yml (create in services/voting/)
version: '3.8'
services:
  voting-service:
    build: .
    ports:
      - "8086:8086"
    environment:
      - CATALOGUE_SERVICE_URL=http://catalogue-service:5000/api/products
    depends_on:
      - catalogue-service
    networks:
      - crafty-network

  catalogue-service:
    # ... catalogue service config
    networks:
      - crafty-network

networks:
  crafty-network:
    driver: bridge
```

The service will be available at [http://localhost:8086](http://localhost:8086)

---

## Quick Reference: Switching Between H2 and MongoDB

### Step-by-Step: Local (H2 only)
1. **H2 (default):**
  - No extra setup needed.
  - Build and run as described above.

### Step-by-Step: Docker Compose
1. **MongoDB (always used in Compose):**
  - Run: `docker-compose up --build`
  - Compose will start both MongoDB and the voting service. No environment variable needed.

---

## Configuration Options

### Application Properties
Key configuration options in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8086

# Catalogue Service Integration
catalogue.service-url=http://localhost:5000/api/products

# H2 Database (default)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true

# MongoDB (when using mongodb profile)
spring.data.mongodb.uri=mongodb://localhost:27017/voting
spring.data.mongodb.database=voting
```

### Profiles
- **default**: Uses H2 in-memory database
- **mongodb**: Uses MongoDB (requires MongoDB instance)

---

## Troubleshooting

### Common Issues

1. **Port 8086 already in use:**
   ```sh
   # Find process using port
   lsof -i :8086  # Linux/Mac
   netstat -ano | findstr :8086  # Windows

   # Kill process or change port
   export SERVER_PORT=8087
   ```

2. **Cannot connect to Catalogue Service:**
   - Ensure catalogue service is running
   - Check network connectivity in Docker
   - Verify CATALOGUE_SERVICE_URL configuration

3. **Database connection issues:**
   - For H2: Usually works out of the box
   - For MongoDB: Ensure MongoDB is running and accessible

### Logs and Debugging
```sh
# View application logs
docker logs voting-service

# View logs with follow
docker logs -f voting-service

# Access H2 console (when using H2)
# Visit: http://localhost:8086/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (leave empty)
```

---

## Notes
- Make sure no other service is running on port 8086.
- You can configure the port and other settings in `src/main/resources/application.properties`.
- The Maven Wrapper (`mvnw`/`mvnw.cmd`) ensures consistent Maven builds even if Maven is not installed globally.
- For production deployments, consider using external databases and proper security configurations.

