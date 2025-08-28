# Crafty Frontend Service - Complete Setup Guide

## Overview
The Frontend Service is a Node.js/Express.js microservice that serves as the main user interface for the Crafty origami marketplace. It acts as an API gateway, proxying requests to the Catalogue, Voting, and Recommendation services while providing a unified user experience.

## Prerequisites
- **Node.js 21.x.x or higher** installed
- **npm** (comes with Node.js)
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Local Environment Setup

### 1. Clone and Navigate
```sh
git clone <repository-url>
cd Crafty/services/frontend
```

### 2. Node.js Setup
```sh
# Verify Node.js installation
node --version
# Should show: v21.x.x or higher

# Verify npm installation
npm --version
# Should show: 10.x.x or higher
```

### 3. Environment Variables
Create a `.env` file or set environment variables:

```bash
# Service URLs (for Docker)
PRODUCTS_API_BASE_URI=http://catalogue:5000
RECOMMENDATION_BASE_URI=http://recommendation:8080
VOTING_BASE_URI=http://voting:8086

# Server Configuration
PORT=3000
NODE_ENV=production
```

**Note:** A `.env` file is already created with the correct Docker service URLs.

## How to Build Frontend App

### Local Build & Run

- **Node version:** latest (e.g. 21.x.x)
- **Build Command:**
  ```sh
  npm install
  ```
- **Port:** 3000
- **Launch Command:**
  ```sh
  node app.js
  ```

### Development Mode
```sh
# Install dependencies
npm install

# Run in development mode with auto-restart
npm run dev

# Or use nodemon for auto-restart
npm install -g nodemon
nodemon app.js
```

### Production Mode
```sh
# Build for production
npm run build

# Start production server
npm start
```

## Docker Setup

### Build & Run with Docker

#### 1. Build Docker Image
```sh
# From services/frontend directory
docker build -t crafty-frontend .
```

#### 2. Run Docker Container
```sh
# Basic run
docker run -d \
  --name crafty-frontend \
  -p 3000:3000 \
  crafty-frontend

# With environment variables
docker run -d \
  --name crafty-frontend \
  -p 3000:3000 \
  -e PRODUCTS_API_BASE_URI=http://host.docker.internal:5000 \
  -e VOTING_BASE_URI=http://host.docker.internal:8086 \
  -e RECOMMENDATION_BASE_URI=http://host.docker.internal:8080 \
  crafty-frontend
```

### Individual Docker Container Setup

#### 1. Build the Docker Image
```sh
# From services/frontend directory
docker build -t crafty-frontend .
```

#### 2. Run Individual Container
```sh
# Basic run
docker run -d \
  --name crafty-frontend \
  -p 3000:3000 \
  crafty-frontend

# With custom environment variables
docker run -d \
  --name crafty-frontend \
  -p 3000:3000 \
  -e PRODUCTS_API_BASE_URI=http://host.docker.internal:5000 \
  -e VOTING_BASE_URI=http://host.docker.internal:8086 \
  -e RECOMMENDATION_BASE_URI=http://host.docker.internal:8080 \
  -e PORT=3000 \
  crafty-frontend
```

#### 3. Development with Volume Mounting
```sh
# Mount source code for development
docker run -d \
  --name crafty-frontend \
  -p 3000:3000 \
  -v $(pwd):/app \
  -w /app \
  -e PRODUCTS_API_BASE_URI=http://host.docker.internal:5000 \
  -e VOTING_BASE_URI=http://host.docker.internal:8086 \
  -e RECOMMENDATION_BASE_URI=http://host.docker.internal:8080 \
  node:18-alpine \
  sh -c "npm install && npm start"
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
  --name catalogue \
  --network crafty-network \
  -p 5000:5000 \
  crafty-catalogue

# 2. Start Voting Service
docker run -d \
  --name voting \
  --network crafty-network \
  -p 8086:8086 \
  crafty-voting

# 3. Start Recommendation Service
docker run -d \
  --name recommendation \
  --network crafty-network \
  -p 8080:8080 \
  crafty-recommendation

# 4. Start Frontend Service (connected to all)
docker run -d \
  --name crafty-frontend \
  --network crafty-network \
  -p 3000:3000 \
  -e PRODUCTS_API_BASE_URI=http://catalogue:5000 \
  -e VOTING_BASE_URI=http://voting:8086 \
  -e RECOMMENDATION_BASE_URI=http://recommendation:8080 \
  crafty-frontend
```

#### Inspect Network
```sh
# Check network details
docker network inspect crafty-network

# List containers on network
docker network ls
```

### Docker Compose Setup (Recommended)

#### Quick Start
```bash
# From services/frontend directory
docker-compose up -d

# View logs
docker-compose logs -f frontend

# Check service health
docker-compose ps
```

#### Run with Full Stack (for testing)
```bash
# Run frontend with all dependent services
docker-compose --profile full-stack up -d
```

#### Stop Services
```bash
docker-compose down
```

#### Docker Compose Configuration

The `docker-compose.yml` file includes:
- **Frontend Service**: Node.js/Express.js application on port 3000
- **Catalogue Service**: Optional for standalone testing
- **Voting Service**: Optional for standalone testing
- **Recommendation Service**: Optional for standalone testing
- **Health Checks**: Automatic service monitoring
- **Networking**: Isolated container network

#### Environment Variables
```bash
# Service URLs (for Docker - RECOMMENDED)
PRODUCTS_API_BASE_URI=http://catalogue:5000     # Catalogue service URL
RECOMMENDATION_BASE_URI=http://recommendation:8080   # Recommendation service URL
VOTING_BASE_URI=http://voting:8086           # Voting service URL

# Alternative: Local development URLs
# PRODUCTS_API_BASE_URI=http://localhost:5000
# RECOMMENDATION_BASE_URI=http://localhost:8080
# VOTING_BASE_URI=http://localhost:8086
NODE_ENV=production                             # Environment mode
PORT=3000                                       # Service port
```

#### Service Health Checks
```bash
# Check service status
docker-compose ps

# View health logs
docker-compose logs frontend

# Manual health check
curl http://localhost:3000
```

### Individual Docker Container Setup

#### 1. Build Docker Image
```bash
# From services/frontend directory
docker build -t crafty-frontend .
```

#### 2. Run Standalone
```bash
docker run -d \
  --name crafty-frontend \
  -p 3000:3000 \
  -e PRODUCTS_API_BASE_URI=http://host.docker.internal:5000 \
  -e RECOMMENDATION_BASE_URI=http://host.docker.internal:8080 \
  -e VOTING_BASE_URI=http://host.docker.internal:8086 \
  crafty-frontend
```

#### 3. Run with Dependent Services
```bash
# Start all services from services/ directory
cd ..
docker-compose up -d
```

### Troubleshooting Docker Compose

#### Service Won't Start
```bash
# Check service logs
docker-compose logs frontend

# Verify environment variables
docker-compose exec frontend env

# Check container health
docker-compose ps
```

#### Backend Connection Issues
```bash
# Check backend service logs
docker-compose logs catalogue voting recommendation

# Test backend connectivity
docker-compose exec frontend curl http://localhost:5000/api/products
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
docker-compose up -d

# View logs
docker-compose logs -f frontend

# Stop all services
docker-compose down
```

### Environment Variables

The container expects these environment variables:

```bash
# Service URLs (from config.json)
PRODUCTS_API_BASE_URI=http://catalogue:5000
RECOMMENDATION_BASE_URI=http://recommendation:8080
VOTING_BASE_URI=http://voting:8086

# Optional: Port configuration
PORT=3000
NODE_ENV=production
```

### Docker Image Details

#### Multi-Stage Build Benefits
- **Builder Stage**: Installs all dependencies and prepares the app
- **Production Stage**: Creates minimal runtime image (~60MB vs ~250MB)
- **Security**: Runs as non-root user with proper signal handling
- **Performance**: Optimized layer caching and health checks

#### Image Size Optimization
- **Without multi-stage**: ~250MB (includes dev dependencies)
- **With multi-stage**: ~80MB (production-only dependencies)
- **Alpine Linux**: Further reduces to ~60MB

### Debugging Docker Containers

#### Check Container Logs
```sh
docker logs crafty-frontend
```

#### Access Container Shell
```sh
docker exec -it crafty-frontend sh
```

#### Health Check
```sh
docker ps  # Check STATUS column for health
docker inspect crafty-frontend | grep -A 10 "Health"
```

#### View Running Processes
```sh
docker top crafty-frontend
```

### Production Considerations

#### Resource Limits
```sh
docker run -d \
  --name crafty-frontend \
  --memory="256m" \
  --cpus="0.5" \
  -p 3000:3000 \
  crafty-frontend
```

#### Restart Policy
```sh
docker run -d \
  --name crafty-frontend \
  --restart unless-stopped \
  -p 3000:3000 \
  crafty-frontend
```

#### Logging
```sh
# Mount log volume
docker run -d \
  --name crafty-frontend \
  -v /var/log/crafty-frontend:/app/logs \
  -p 3000:3000 \
  crafty-frontend
```

### Troubleshooting

#### Common Issues

1. **Port already in use**
   ```sh
   # Find process using port 3000
   netstat -tulpn | grep :3000
   # Kill the process or use different port
   docker run -p 3001:3000 crafty-frontend
   ```

2. **Environment variables not working**
   ```sh
   # Check environment inside container
   docker exec crafty-frontend env
   ```

3. **Service connectivity issues**
   ```sh
   # Test connection to other services
   docker exec crafty-frontend curl http://catalogue:5000/api/products
   ```

4. **Permission issues**
   ```sh
   # Check file permissions
   docker exec crafty-frontend ls -la /app
   ```

#### Health Check Failures
```sh
# Manual health check
docker exec crafty-frontend node --version

# Check health status
docker inspect crafty-frontend | jq '.[].State.Health'
```

### Docker Compose for Multi-Service Setup

#### Create docker-compose.yml
```yaml
# docker-compose.yml (create in services/frontend/)
version: '3.8'
services:
  frontend-service:
    build: .
    ports:
      - "3000:3000"
    environment:
      - CATALOGUE_BASE_URI=http://catalogue-service:5000
      - VOTING_BASE_URI=http://voting-service:8086
      - RECOMMENDATION_BASE_URI=http://recommendation-service:8080
    depends_on:
      - catalogue-service
      - voting-service
      - recommendation-service
    networks:
      - crafty-network

  catalogue-service:
    # ... catalogue service config
    networks:
      - crafty-network

  voting-service:
    # ... voting service config
    networks:
      - crafty-network

  recommendation-service:
    # ... recommendation service config
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

## Configuration

### config.json
The service uses a configuration file (`config.json`) for service URLs:

```json
{
  "version": "1.0.0",
  "productsApiBaseUri": "http://localhost:5000",
  "recommendationBaseUri": "http://localhost:8080",
  "votingBaseUri": "http://localhost:8086"
}
```

### Environment Variables
- `PRODUCTS_API_BASE_URI`: Base URL for catalogue service (default: http://catalogue:5000)
- `VOTING_BASE_URI`: Base URL for voting service (default: http://voting:8086)
- `RECOMMENDATION_BASE_URI`: Base URL for recommendation service (default: http://recommendation:8080)
- `PORT`: Server port (default: 3000)
- `NODE_ENV`: Environment mode (development/production)

## API Endpoints

### Frontend Routes
- `GET /` - Main application page
- `GET /api/products` - Get all products (proxied to catalogue)
- `GET /api/origamis/:id/votes` - Get vote count (proxied to voting)
- `POST /api/origamis/:id/vote` - Vote for origami (proxied to voting)
- `GET /api/service-status` - Overall service status
- `GET /api/daily-origami` - Get daily origami recommendation
- `GET /recommendation-status` - Recommendation service status
- `GET /votingservice-status` - Voting service status

### Static Assets
- `/static/*` - Static files (CSS, JS, images)
- `/public/*` - Public assets

## Features

### User Interface
- **Product Catalog**: Browse origami products with pagination
- **Voting System**: Vote for favorite origamis
- **Daily Recommendations**: View daily origami recommendations
- **Service Status Dashboard**: Monitor all microservices health
- **Responsive Design**: Mobile-friendly interface

### API Gateway Functionality
- **Request Proxying**: Routes requests to appropriate microservices
- **Error Handling**: Unified error responses
- **Load Balancing**: Can be extended for multiple instances
- **Caching**: Can be implemented for performance

## Testing

### Local Testing
```sh
# Install test dependencies
npm install --include=dev

# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Run specific test
npm test -- --grep "test name"
```

### API Testing
```sh
# Test main page
curl http://localhost:3000/

# Test products API
curl http://localhost:3000/api/products

# Test service status
curl http://localhost:3000/api/service-status

# Test voting
curl -X POST http://localhost:3000/api/origamis/1/vote
```

### Browser Testing
1. Open browser to `http://localhost:3000`
2. Test product browsing
3. Test voting functionality
4. Check service status dashboard

## Troubleshooting

### Common Issues

1. **Port 3000 already in use:**
   ```sh
   # Find process using port
   lsof -i :3000  # Linux/Mac
   netstat -ano | findstr :3000  # Windows

   # Kill process or change port
   export PORT=3001
   ```

2. **Cannot connect to backend services:**
   ```sh
   # Check if services are running
   curl http://localhost:5000/api/products  # Catalogue
   curl http://localhost:8086/api/origamis/status  # Voting
   curl http://localhost:8080/api/recommendation-status  # Recommendation

   # Check Docker network connectivity
   docker network inspect crafty-network
   ```

3. **npm install fails:**
   ```sh
   # Clear npm cache
   npm cache clean --force

   # Clear node_modules and reinstall
   rm -rf node_modules package-lock.json
   npm install
   ```

4. **Module not found errors:**
   ```sh
   # Reinstall dependencies
   rm -rf node_modules package-lock.json
   npm install
   ```

### Logs and Debugging
```sh
# View application logs
docker logs frontend-service

# View logs with follow
docker logs -f frontend-service

# Debug mode
NODE_ENV=development npm start

# Check Node.js process
ps aux | grep node
```

## Development Workflow

### 1. Code Changes
```sh
# Make changes to files
# Install new dependencies if needed
npm install

# Test locally
npm start

# Run tests
npm test
```

### 2. Docker Development
```sh
# Build and run with volume mounting
docker run -d \
  --name frontend-service \
  -p 3000:3000 \
  -v $(pwd):/app \
  -w /app \
  node:21 \
  npm start
```

### 3. Hot Reload Setup
```sh
# Install nodemon globally
npm install -g nodemon

# Or add to dev dependencies
npm install --save-dev nodemon

# Add to package.json scripts
{
  "scripts": {
    "dev": "nodemon app.js"
  }
}

# Run with hot reload
npm run dev
```

## Deployment

### Production Considerations
- Set `NODE_ENV=production`
- Use process manager (PM2)
- Configure reverse proxy (nginx)
- Set up SSL/TLS
- Implement logging
- Configure resource limits
- Use Docker secrets for sensitive data

### Production Docker Run
```sh
docker run -d \
  --name frontend-service \
  --restart unless-stopped \
  -p 3000:3000 \
  -e NODE_ENV=production \
  -e CATALOGUE_BASE_URI=http://catalogue-service:5000 \
  -e VOTING_BASE_URI=http://voting-service:8086 \
  -e RECOMMENDATION_BASE_URI=http://recommendation-service:8080 \
  --memory="512m" \
  --cpus="1.0" \
  crafty-frontend-service
```

### PM2 Deployment
```sh
# Install PM2
npm install -g pm2

# Start with PM2
pm2 start app.js --name "crafty-frontend"

# Save PM2 configuration
pm2 save

# Generate startup script
pm2 startup
```

## Service Dependencies

### Required Services
- **Catalogue Service** (Port 5000): Provides product data
- **Voting Service** (Port 8086): Handles voting functionality
- **Recommendation Service** (Port 8080): Provides daily recommendations

### Service Communication
- Uses HTTP REST APIs for inter-service communication
- Implements error handling for service unavailability
- Provides fallback behavior when services are down

## Performance Optimization

### Frontend Optimizations
- **Static File Caching**: Configurable cache headers
- **Compression**: Enable gzip compression
- **Minification**: Minify CSS/JS for production
- **CDN**: Use CDN for static assets
- **Lazy Loading**: Implement lazy loading for images

### Monitoring
- **Health Checks**: Built-in service status endpoints
- **Logging**: Comprehensive logging for debugging
- **Metrics**: Can be extended with monitoring tools
- **Error Tracking**: Error logging and tracking

## Security Considerations

### Basic Security
- Input validation and sanitization
- CORS configuration
- Rate limiting (can be implemented)
- HTTPS enforcement in production
- Secure headers implementation

### Docker Security
- Non-root user execution
- Minimal base image
- No sensitive data in images
- Regular security updates

## Contributing

### Development Setup
1. Fork the repository
2. Clone your fork
3. Create feature branch
4. Make changes
5. Test locally
6. Submit pull request

### Code Standards
- Use ESLint for code linting
- Follow Node.js best practices
- Write tests for new features
- Update documentation

## Notes
- Ensure all dependent services are running before starting the frontend
- The service acts as an API gateway for the microservices architecture
- Static files are served from the `/public` directory
- The application uses EJS templating engine
- Service status monitoring is built-in for operational visibility
  