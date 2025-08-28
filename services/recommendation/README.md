
# Crafty Recommendation Service - Complete Setup Guide

## Overview
The Recommendation Service is a Go microservice that provides daily origami recommendations. It fetches origami data from the Catalogue Service and returns a random origami as the "origami of the day".

## Prerequisites
- **Go 1.25+** installed and configured
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Quick Start with Docker Compose (Recommended)

### 1. Environment Setup
```bash
# Navigate to recommendation service directory
cd services/recommendation

# Copy environment template
cp .env.example .env

# Edit .env file with your preferred settings
# See Environment Configuration section below
```

### 2. Choose Your Setup Mode

#### Option A: Connect to Existing Catalogue Service
```bash
# Edit .env file to point to your catalogue service
# CATALOGUE_API_URL=http://your-catalogue-service:5000/api/products

# Start only the recommendation service
docker-compose up -d
```

#### Option B: Run with Standalone Catalogue Service
```bash
# Start both recommendation and catalogue services
docker-compose --profile with-catalogue up -d

# The catalogue service will be automatically configured with JSON mode
```

### 3. Verify Service is Running
```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs -f recommendation

# Test the service
curl http://localhost:8080/api/recommendation-status
curl http://localhost:8080/api/origami-of-the-day
```

## Environment Configuration

### Service Configuration
```bash
# Application Version
APP_VERSION=1.0.0

# Catalogue Service URL
CATALOGUE_API_URL=http://localhost:5000/api/products

# Service Port
PORT=8080

# Development Mode
GIN_MODE=debug
```

### Standalone Catalogue Configuration
When using `--profile with-catalogue`, additional environment variables are available:
```bash
# Database Configuration
DB_HOST=catalogue-db
DB_NAME=catalogue
DB_USER=crafty
DB_PASSWORD=crafty

# Data Source Mode
DATA_SOURCE=json  # or 'db' for PostgreSQL

# Port Configuration
CATALOGUE_PORT=5000
POSTGRES_PORT=5432
```

## API Endpoints

### Core Endpoints
- `GET /` - Web interface with service information
- `GET /api/origami-of-the-day` - Returns a random origami recommendation
- `GET /api/recommendation-status` - Service health check

### Response Examples

#### Get Origami of the Day
```bash
curl http://localhost:8080/api/origami-of-the-day
```
Response:
```json
{
  "id": 1,
  "name": "Orange Fox",
  "category": "Animal",
  "difficulty": "Easy",
  "tags": ["fox", "animal", "orange"],
  "short_description": "A clever little fox with sharp folds.",
  "description": "A clever little fox sitting upright, its sharp folds capturing the sly tilt of its ears and the mischief in its stance.",
  "image_url": "/images/origami/001-origami-orange-fox.jpg",
  "created_at": "2025-08-25"
}
```

#### Service Status
```bash
curl http://localhost:8080/api/recommendation-status
```
Response:
```json
{
  "status": "operational",
  "service": "recommendation",
  "timestamp": "2025-08-28T10:00:00Z"
}
```

## Service Dependencies

**Note:** This recommendation service depends on the catalogue service. It fetches product data from the catalogue service via its API (`/api/products`).

- Make sure the catalogue service is running and accessible before starting the recommendation service
- By default, it expects the catalogue API at `http://localhost:5000/api/products`
- You can override this using the `CATALOGUE_API_URL` environment variable
- The service includes error handling for catalogue service unavailability

### Error Handling
The service gracefully handles various error scenarios:
- **Catalogue service unavailable**: Returns appropriate error messages
- **Empty product data**: Handles cases where no products are available
- **Network timeouts**: 10-second timeout for external API calls
- **Invalid responses**: Validates JSON content and structure

## Service Features

### Error Handling & Resilience
- **Timeout Protection**: HTTP client has 10-second timeout for external API calls
- **Content Validation**: Validates response content type from catalogue service
- **Empty Response Handling**: Gracefully handles cases where no data is available
- **Structured Logging**: Comprehensive logging for debugging and monitoring

### API Endpoints
- `GET /api/origami-of-the-day` - Returns a random origami recommendation
- `GET /api/recommendation-status` - Service health check with timestamp
- `GET /` - Web interface with system information

### Alternative Startup Methods
The service provides two ways to start:

1. **Standard Method** (default in main.go):
   ```go
   func main() {
       // Standard Gin router setup with graceful shutdown
   }
   ```

2. **Alternative Method** (using api.StartAPI()):
   ```go
   func main() {
       api.StartAPI() // Uncomment this line in main.go
   }
   ```

## How to Build & Run Recommendation App

### Local Build & Run

- **Build Tool:** Go (Tested with version 1.25+)
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

## Docker Compose Setup (Recommended)

### Quick Start
```bash
# From services/recommendation directory
# Docker Compose automatically reads ../.env
docker-compose up -d

# View logs
docker-compose logs -f recommendation

# Check service health
docker-compose ps
```

### Run with Catalogue Service
```bash
# Run both recommendation and catalogue services
docker-compose --profile with-catalogue up -d
```

### Stop Services
```bash
docker-compose down
```

### Docker Compose Configuration

The `docker-compose.yml` file includes:
- **Recommendation Service**: Go application on port 8080
- **Catalogue Service**: Optional for standalone testing
- **Health Checks**: Automatic service monitoring
- **Networking**: Isolated container network

#### Environment Variables
```bash
CATALOGUE_API_URL=http://localhost:5000  # Catalogue service URL
PORT=8080                                   # Service port
```

#### Service Health Checks
```bash
# Check service status
docker-compose ps

# View health logs
docker-compose logs recommendation

# Manual health check
curl http://localhost:8080
```

### Individual Docker Container Setup

#### 1. Build Docker Image
```bash
# From services/recommendation directory
docker build -t crafty-recommendation .
```

#### 2. Run Standalone
```bash
docker run -d \
  --name crafty-recommendation \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://localhost:5000 \
  crafty-recommendation
```

#### 3. Run with Catalogue Service
```bash
# Start catalogue service first
docker run -d \
  --name crafty-catalogue \
  -p 5000:5000 \
  crafty-catalogue

# Start recommendation service
docker run -d \
  --name crafty-recommendation \
  -p 8080:8080 \
  --link crafty-catalogue:catalogue \
  -e CATALOGUE_API_URL=http://catalogue:5000 \
  crafty-recommendation
```

### Troubleshooting Docker Compose

#### Service Won't Start
```bash
# Check service logs
docker-compose logs recommendation

# Verify environment variables
docker-compose exec recommendation env

# Check container health
docker-compose ps
```

#### Catalogue Connection Issues
```bash
# Check catalogue service
docker-compose logs catalogue

# Test catalogue connectivity
docker-compose exec recommendation curl http://localhost:5000/api/products
```

#### Reset Everything
```bash
# Stop and remove containers
docker-compose down

# Clean rebuild
docker-compose build --no-cache
docker-compose up -d
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

## Testing

### Running Tests
```sh
# Run all tests
go test ./...

# Run tests with verbose output
go test -v ./...

# Run specific test file
go test -v ./tests/

# Run tests with coverage
go test -cover ./...
```

### Test Coverage
The service includes tests for:
- API endpoint functionality
- Service status endpoint
- Error handling scenarios
- Alternative startup methods

### Manual Testing
```sh
# Test health endpoint
curl http://localhost:8080/api/recommendation-status

# Test origami endpoint
curl http://localhost:8080/api/origami-of-the-day

# Test web interface
curl http://localhost:8080/
```

## Recent Improvements

### ✅ Version 1.0.1 Updates
- **Enhanced Error Handling**: Added timeout protection and content validation
- **Improved Logging**: Structured logging for better debugging
- **Alternative Startup**: Added `StartAPI()` function for flexible deployment
- **Graceful Shutdown**: Added signal handling for clean service shutdown
- **Better Testing**: Enhanced test coverage with multiple scenarios
- **Documentation**: Updated README with new features and examples

### 🔧 Configuration Files
- `.env.example` - Environment variable template
- `.dockerignore` - Optimized Docker build context
- `.gitignore` - Comprehensive Git ignore rules

The service is now production-ready with proper error handling, logging, and testing capabilities!

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
 