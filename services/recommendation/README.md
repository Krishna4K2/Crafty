
# Crafty Recommendation Service - Complete Setup Guide

## Overview
The Recommendation Service is a Go microservice that provides daily origami recommendations. It fetches origami data from the Catalogue Service and returns a random origami as the "origami of the day".

## Prerequisites
- **Go 1.20+** installed and configured
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Local Environment Setup

### 1. Clone and Navigate
```sh
git clone <repository-url>
cd Crafty/services/recommendation
```

### 2. Go Setup
```sh
# Verify Go installation
go version
# Should show: go version go1.20.x or higher

# Initialize Go modules (if not already done)
go mod tidy

# Verify module dependencies
go list -m all
```

### 3. Environment Variables
```sh
# Catalogue service URL (default: http://localhost:5000/api/products)
export CATALOGUE_API_URL=http://localhost:5000/api/products

# Port configuration (default: 8080)
export PORT=8080
```

## How to Build & Run Recommendation App

### Local Build & Run

- **Build Tool:** Go (Tested with version 1.20+)
- **Build Command:**
  ```sh
  go build -o app
  ```
- **Port:** 8080
- **Launch Command:**
  ```sh
  ./app
  ```
- **Or Run directly**
  ```sh
  go run main.go
  ```

### Development Mode
```sh
# Run with hot reload (requires air or similar tool)
go install github.com/cosmtrek/air@latest
air

# Or use go run with file watching
go run main.go
```

## Docker Setup

### Build & Run with Docker

1. **Build Docker Image:**
   ```sh
   docker build -t recommendation-app .
   ```
2. **Run Docker Container:**
   ```sh
   docker run -d -p 8080:8080 -e CATALOGUE_API_URL="http://<catalogue-host>:5000/api/products" recommendation-app
   ```

### Individual Docker Container Setup

#### 1. Build the Docker Image
```sh
# From services/recommendation directory
docker build -t crafty-recommendation-service .
```

#### 2. Run Individual Container
```sh
# Basic run
docker run -d \
  --name recommendation-service \
  -p 8080:8080 \
  crafty-recommendation-service

# With custom environment variables
docker run -d \
  --name recommendation-service \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://host.docker.internal:5000/api/products \
  -e PORT=8080 \
  crafty-recommendation-service
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

# 2. Start Recommendation Service (connected to catalogue)
docker run -d \
  --name recommendation-service \
  --network crafty-network \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://catalogue-service:5000/api/products \
  crafty-recommendation-service

# 3. Start Frontend Service (connected to both)
docker run -d \
  --name frontend-service \
  --network crafty-network \
  -p 3000:3000 \
  -e CATALOGUE_BASE_URI=http://catalogue-service:5000 \
  -e RECOMMENDATION_BASE_URI=http://recommendation-service:8080 \
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

#### Create docker-compose.yml
```yaml
# docker-compose.yml (create in services/recommendation/)
version: '3.8'
services:
  recommendation-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      - CATALOGUE_API_URL=http://catalogue-service:5000/api/products
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

#### Run with Docker Compose
```sh
docker-compose up --build
```

## Service Dependency

**Note:** This recommendation service depends on the catalogue service. It fetches product data from the catalogue service via its API (`/api/products`).

- Make sure the catalogue service is running and accessible before starting the recommendation service.
- By default, it expects the catalogue API at `http://localhost:5000/api/products`. You can override this using the `CATALOGUE_API_URL` environment variable.

## Environment Variables

- `CATALOGUE_API_URL` (optional): Set this to the catalogue service API endpoint if not running on localhost:5000.
- `PORT` (optional): Set the port for the service (default: 8080).

## Endpoints

- `/` : Home page
- `/api/origami-of-the-day` : Get a random origami product from catalogue
- `/api/recommendation-status` : Service status

## Configuration

### config.json
The service uses a configuration file (`config.json`) for basic settings:

```json
{
  "version": "1.0.0"
}
```

## Testing

### Local Testing
```sh
# Run tests
go test ./...

# Run with verbose output
go test -v ./...

# Run specific test
go test -run TestSpecificFunction
```

### API Testing
```sh
# Test health endpoint
curl http://localhost:8080/api/recommendation-status

# Test origami of the day
curl http://localhost:8080/api/origami-of-the-day

# Test home page
curl http://localhost:8080/
```

## Troubleshooting

### Common Issues

1. **Cannot connect to Catalogue Service:**
   ```sh
   # Check if catalogue service is running
   curl http://localhost:5000/api/products

   # Check Docker network connectivity
   docker network inspect crafty-network
   ```

2. **Port 8080 already in use:**
   ```sh
   # Find process using port
   lsof -i :8080  # Linux/Mac
   netstat -ano | findstr :8080  # Windows

   # Change port
   export PORT=8081
   ```

3. **Go module issues:**
   ```sh
   # Clean module cache
   go clean -modcache

   # Reinitialize modules
   rm go.mod go.sum
   go mod init recommendation
   go mod tidy
   ```

### Logs and Debugging
```sh
# View application logs
docker logs recommendation-service

# View logs with follow
docker logs -f recommendation-service

# Debug mode (if implemented)
docker run -d \
  --name recommendation-service \
  -p 8080:8080 \
  -e DEBUG=true \
  crafty-recommendation-service
```

## Development Workflow

### 1. Code Changes
```sh
# Make changes to Go files
# Test locally
go run main.go

# Build and test
go build -o app
./app
```

### 2. Docker Development
```sh
# Build and run with volume mounting for development
docker run -d \
  --name recommendation-service \
  -p 8080:8080 \
  -v $(pwd):/app \
  -w /app \
  crafty-recommendation-service \
  go run main.go
```

## Deployment

### Production Considerations
- Use environment variables for configuration
- Implement proper logging
- Add health checks
- Use Docker secrets for sensitive data
- Configure proper resource limits
- Implement graceful shutdown

### Production Docker Run
```sh
docker run -d \
  --name recommendation-service \
  --restart unless-stopped \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://catalogue-service:5000/api/products \
  -e PORT=8080 \
  --memory="256m" \
  --cpus="0.5" \
  crafty-recommendation-service
```

## Notes
- The service requires Go 1.20+ for optimal performance
- Ensure the catalogue service is running before starting this service
- The service fetches data from the catalogue service on each request
- For high-traffic scenarios, consider implementing caching
- The random selection algorithm can be enhanced for better recommendation logic
 