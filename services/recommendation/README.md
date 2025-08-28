# Crafty Recommendation Service

A Go microservice that provides daily origami recommendations by fetching data from the Catalogue Service and returning a random origami as the "origami of the day".

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Environment Configuration](#environment-configuration)
- [API Endpoints](#api-endpoints)
- [Service Dependencies](#service-dependencies)
- [Docker Setup](#docker-setup)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Development](#development)
- [Deployment](#deployment)

## Overview
The Recommendation Service is built with:
- **Go 1.25+** with Gin web framework
- **Docker** containerization with multi-stage builds
- **Health checks** and graceful shutdown
- **Error handling** for external service dependencies
- **Structured logging** for monitoring and debugging

## Prerequisites
- **Go 1.25+** installed and configured
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Quick Start

### 1. Environment Setup
```bash
# Navigate to recommendation service directory
cd services/recommendation

# Copy environment template
cp .env.example .env

# Edit .env file with your preferred settings
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

**Important:** This service depends on the Catalogue Service and fetches product data via its API (`/api/products`).

### Requirements
- Catalogue service must be running and accessible
- Default expects catalogue API at `http://localhost:5000/api/products`
- Override using `CATALOGUE_API_URL` environment variable

### Error Handling
The service gracefully handles:
- **Catalogue service unavailable**: Returns appropriate error messages
- **Empty product data**: Handles cases where no products are available
- **Network timeouts**: 10-second timeout for external API calls
- **Invalid responses**: Validates JSON content and structure

## Docker Setup

### Build & Run with Docker Compose (Recommended)

#### Quick Start
```bash
# From services/recommendation directory
docker-compose up -d

# View logs
docker-compose logs -f recommendation

# Check service health
docker-compose ps
```

#### Run with Catalogue Service
```bash
# Run both services together
docker-compose --profile with-catalogue up -d
```

#### Stop Services
```bash
docker-compose down
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

### Docker Networking

#### Create Network for Multi-Service Setup
```bash
# Create custom network
docker network create crafty-network

# Run services on same network
docker run -d \
  --name catalogue-service \
  --network crafty-network \
  -p 5000:5000 \
  crafty-catalogue

docker run -d \
  --name recommendation-service \
  --network crafty-network \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://catalogue-service:5000/api/products \
  crafty-recommendation
```

## Testing

### Local Testing
```bash
# Run all tests
go test ./...

# Run with verbose output
go test -v ./...

# Run specific test file
go test -v ./tests/

# Run with coverage
go test -cover ./...
```

### API Testing
```bash
# Test health endpoint
curl http://localhost:8080/api/recommendation-status

# Test origami endpoint
curl http://localhost:8080/api/origami-of-the-day

# Test web interface
curl http://localhost:8080/
```

### Manual Testing Checklist
- [ ] Service starts without errors
- [ ] Health endpoint returns operational status
- [ ] Origami endpoint returns valid JSON
- [ ] Web interface loads correctly
- [ ] Error handling works for invalid catalogue URLs

## Troubleshooting

### Common Issues

#### 1. Cannot Connect to Catalogue Service
```bash
# Check if catalogue service is running
curl http://localhost:5000/api/products

# Check Docker network connectivity
docker network inspect crafty-network

# Verify environment variables
docker-compose exec recommendation env | grep CATALOGUE
```

#### 2. Port Already in Use
```bash
# Find process using port
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows

# Change port in .env file
PORT=8081
```

#### 3. Go Module Issues
```bash
# Clean module cache
go clean -modcache

# Reinitialize modules
rm go.mod go.sum
go mod init recommendation
go mod tidy
```

### Logs and Debugging
```bash
# View application logs
docker-compose logs recommendation

# Follow logs in real-time
docker-compose logs -f recommendation

# Check container health
docker-compose ps
```

### Reset Everything
```bash
# Stop and remove containers
docker-compose down

# Clean rebuild
docker-compose build --no-cache
docker-compose up -d
```

## Development

### Local Development
```bash
# Run directly
go run main.go

# Build and run
go build -o app
./app
```

### Docker Development
```bash
# Build with volume mounting for live reload
docker run -d \
  --name recommendation-dev \
  -p 8080:8080 \
  -v $(pwd):/app \
  -w /app \
  crafty-recommendation \
  go run main.go
```

### Code Structure
```
services/recommendation/
├── main.go              # Application entry point
├── api/
│   └── api.go          # API handlers and routes
├── data/
│   └── data.go         # External API communication
├── static/              # Static assets (CSS, images)
├── templates/           # HTML templates
├── tests/               # Unit tests
├── docker-compose.yml   # Docker orchestration
├── Dockerfile          # Container build configuration
├── entrypoint.sh       # Container startup script
└── config.json         # Application configuration
```

### Configuration Files
- `config.json`: Application version and basic settings
- `.env`: Environment variables for local development
- `.env.example`: Template for environment configuration

## Deployment

### Production Considerations
- [ ] Use environment variables for configuration
- [ ] Implement proper logging and monitoring
- [ ] Add health checks and readiness probes
- [ ] Use Docker secrets for sensitive data
- [ ] Configure resource limits and requests
- [ ] Implement graceful shutdown procedures
- [ ] Set up proper networking and service discovery

### Production Docker Run
```bash
docker run -d \
  --name recommendation-service \
  --restart unless-stopped \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://catalogue-service:5000/api/products \
  -e PORT=8080 \
  --memory="256m" \
  --cpus="0.5" \
  crafty-recommendation
```

## Notes
- Service requires Go 1.25+ for optimal performance
- Ensure catalogue service is running before starting
- Service fetches data from catalogue on each request
- For high-traffic scenarios, consider implementing caching
- Random selection algorithm can be enhanced for better recommendations
