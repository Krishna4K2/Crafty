
# Crafty Catalogue Service - Complete Setup Guide

## Overview
The Catalogue Service is a Python Flask microservice that manages the origami product catalog. It supports both JSON file-based storage and PostgreSQL database storage, making it flexible for different deployment scenarios.

## Prerequisites
- **Python 3.8+** installed
- **pip** (Python package manager)
- **PostgreSQL** (optional, only if using database mode)
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Local Environment Setup

### 1. Clone and Navigate
```sh
git clone <repository-url>
cd Crafty/services/catalogue
```

### 2. Python Environment Setup
```sh
# Verify Python installation
python --version
# Should show: Python 3.8.x or higher

# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# Deactivate when done
deactivate
```

### 3. Environment Configuration
```sh
# Copy environment template
cp .env.example .env

# Edit .env file with your settings
# See section below for configuration options
```

## 1. Choose Your Data Source

You can run the app using either a local JSON file or a PostgreSQL database.

Configuration is handled via the .env file.
```sh
cp .env.example .env # create .env file
```

Here's a sample .env file you can use:
```
# Data source (json or db)
DATA_SOURCE=json

# Database config (used only if DATA_SOURCE=db)
DB_HOST=catalogue-db
DB_NAME=catalogue
DB_USER=devops
DB_PASSWORD=devops
```

**Note**
- Update values in .env as needed (especially DB credentials).
- Never commit real secrets in .env for production projects. For this learning/demo project, we are keeping .env in GitHub.

### Option A: Use JSON File
1. Open `.env`.
2. Set:
   ```env
   DATA_SOURCE=json
   ```
3. The app will read product data from `products.json`.

### Option B: Use PostgreSQL Database
1. Open `.env`.
2. Set:
   ```env
   DATA_SOURCE=db
   ```
3. Ensure your database credentials in `.env` match your database or Docker Compose settings:
   ```
   DB_HOST=catalogue-db
   DB_NAME=catalogue
   DB_USER=devops
   DB_PASSWORD=devops
   ```
4. The app will read product data from the PostgreSQL database.

## 3. Install Dependencies & Build

```sh
# Install Python dependencies
pip install -r requirements.txt

# Verify installation
pip list
```

## 4. Run the App Locally

### Development Mode
```sh
# Run with Flask development server
python app.py
```

### Production Mode
```sh
# Run with Gunicorn (recommended for production)
gunicorn app:app --bind 0.0.0.0:5000

# Or with multiple workers
gunicorn app:app --bind 0.0.0.0:5000 --workers 4
```

## Docker Setup

### Build & Run with Docker

#### 1. Build Docker Image
```sh
# From services/catalogue directory
docker build -t crafty-catalogue-service .
```

#### 2. Run Docker Container
```sh
# Basic run with JSON data
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  -e DATA_SOURCE=json \
  crafty-catalogue-service

# With environment file
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  --env-file .env \
  crafty-catalogue-service
```

### Individual Docker Container Setup

#### 1. Build the Docker Image
```sh
# From services/catalogue directory
docker build -t crafty-catalogue-service .
```

#### 2. Run Individual Container
```sh
# JSON mode (default)
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  -e DATA_SOURCE=json \
  crafty-catalogue-service

# Database mode
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  -e DATA_SOURCE=db \
  -e DB_HOST=host.docker.internal \
  -e DB_NAME=catalogue \
  -e DB_USER=devops \
  -e DB_PASSWORD=devops \
  crafty-catalogue-service

# With volume mounting for development
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  -v $(pwd):/app \
  -w /app \
  --env-file .env \
  python:3.11 \
  python app.py
```

#### 3. Run with PostgreSQL
```sh
# First, start PostgreSQL
docker run -d \
  --name postgres-db \
  -p 5432:5432 \
  -e POSTGRES_DB=catalogue \
  -e POSTGRES_USER=devops \
  -e POSTGRES_PASSWORD=devops \
  postgres:15

# Then start catalogue service
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  --link postgres-db:postgres \
  -e DATA_SOURCE=db \
  -e DB_HOST=postgres \
  -e DB_NAME=catalogue \
  -e DB_USER=devops \
  -e DB_PASSWORD=devops \
  crafty-catalogue-service
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
  -e DATA_SOURCE=json \
  crafty-catalogue-service

# 2. Start Voting Service (connected to catalogue)
docker run -d \
  --name voting-service \
  --network crafty-network \
  -p 8086:8086 \
  -e CATALOGUE_SERVICE_URL=http://catalogue-service:5000/api/products \
  crafty-voting-service

# 3. Start Recommendation Service (connected to catalogue)
docker run -d \
  --name recommendation-service \
  --network crafty-network \
  -p 8080:8080 \
  -e CATALOGUE_API_URL=http://catalogue-service:5000/api/products \
  crafty-recommendation-service

# 4. Start Frontend Service (connected to all)
docker run -d \
  --name frontend-service \
  --network crafty-network \
  -p 3000:3000 \
  -e CATALOGUE_BASE_URI=http://catalogue-service:5000 \
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
# From services/catalogue directory
# Docker Compose automatically reads ../.env
docker-compose up -d

# View logs
docker-compose logs -f catalogue

# Check service health
docker-compose ps
```

### Run with PostgreSQL Database
```bash
# Set environment variable for database mode
DATA_SOURCE=db docker-compose up -d

# Or export the variable
export DATA_SOURCE=db
docker-compose up -d
```

### Stop Services
```bash
docker-compose down
```

### Docker Compose Configuration

The `docker-compose.yml` file includes:
- **Catalogue Service**: Python Flask application on port 5000
- **PostgreSQL Database**: Optional persistent storage on port 5432
- **Health Checks**: Automatic service monitoring
- **Networking**: Isolated container network

#### Environment Variables
```bash
DATA_SOURCE=json          # json or db (default: json)
DB_HOST=catalogue-db      # PostgreSQL hostname
DB_NAME=catalogue         # Database name
DB_USER=user             # Database user
DB_PASSWORD=password     # Database password
APP_VERSION=1.0.0        # Application version
```

#### Service Health Checks
```bash
# Check service status
docker-compose ps

# View health logs
docker-compose logs catalogue

# Manual health check
curl http://localhost:5000/api/products
```

### Troubleshooting Docker Compose

#### Database Connection Issues
```bash
# Check database logs
docker-compose logs catalogue-db

# Verify database is ready
docker-compose exec catalogue-db pg_isready -U user -d catalogue

# Restart database
docker-compose restart catalogue-db
```

#### Service Won't Start
```bash
# Check service logs
docker-compose logs catalogue

# Verify environment variables
docker-compose exec catalogue env

# Check container health
docker-compose ps
```

#### Reset Everything
```bash
# Stop and remove containers
docker-compose down

# Remove volumes (WARNING: deletes data)
docker-compose down -v

# Clean rebuild
docker-compose build --no-cache
docker-compose up -d
```

## 5. Run only APP in Docker container
```sh
docker build -t my-python-app . # Build the image
docker run -d -p 5000:5000 --env-file .env  my-python-app # Run container with image
```

## 6. Run the APP and PostgreSQL with Docker Compose

```sh
docker compose --env-file .env up --build -d

docker compose logs # to view the logs
```
- The app will be available at [http://localhost:5000](http://localhost:5000)
- PostgreSQL will run in a separate container and persist data in a Docker volume.

To stop and remove containers:
```sh
docker-compose down
```

## API Endpoints

### Catalogue Endpoints
- `GET /` - Home page
- `GET /api/products` - Get all products
- `GET /api/products/:id` - Get specific product
- `POST /api/products` - Add new product (JSON mode only)
- `PUT /api/products/:id` - Update product (JSON mode only)
- `DELETE /api/products/:id` - Delete product (JSON mode only)

### Service Endpoints
- `GET /health` - Health check
- `GET /status` - Service status

## Configuration Options

### Environment Variables
- `DATA_SOURCE`: `json` or `db` (default: json)
- `DB_HOST`: PostgreSQL host (default: localhost)
- `DB_NAME`: PostgreSQL database name (default: catalogue)
- `DB_USER`: PostgreSQL username (default: devops)
- `DB_PASSWORD`: PostgreSQL password (default: devops)
- `DB_PORT`: PostgreSQL port (default: 5432)

### Sample .env Configurations

#### JSON Mode
```env
DATA_SOURCE=json
```

#### Database Mode
```env
DATA_SOURCE=db
DB_HOST=localhost
DB_NAME=catalogue
DB_USER=devops
DB_PASSWORD=devops
DB_PORT=5432
```

#### Docker Database Mode
```env
DATA_SOURCE=db
DB_HOST=catalogue-db
DB_NAME=catalogue
DB_USER=devops
DB_PASSWORD=devops
DB_PORT=5432
```

## Testing

### Local Testing
```sh
# Run Python tests (if available)
python -m pytest

# Manual API testing
curl http://localhost:5000/api/products
curl http://localhost:5000/health
```

### Docker Testing
```sh
# Test container
docker exec -it catalogue-service curl http://localhost:5000/api/products

# Test from another container
docker run --rm --network crafty-network \
  curlimages/curl \
  http://catalogue-service:5000/api/products
```

## Troubleshooting

### Common Issues

1. **Port 5000 already in use:**
   ```sh
   # Find process using port
   lsof -i :5000  # Linux/Mac
   netstat -ano | findstr :5000  # Windows

   # Kill process or change port
   # Edit app.py to change port
   ```

2. **Database connection issues:**
   ```sh
   # Check PostgreSQL status
   docker ps | grep postgres

   # Check database logs
   docker logs postgres-db

   # Test database connection
   docker exec -it postgres-db psql -U devops -d catalogue
   ```

3. **Permission issues with .env:**
   ```sh
   # Fix file permissions
   chmod 600 .env
   ```

4. **Module import errors:**
   ```sh
   # Reinstall requirements
   pip uninstall -r requirements.txt
   pip install -r requirements.txt
   ```

### Logs and Debugging
```sh
# View application logs
docker logs catalogue-service

# View logs with follow
docker logs -f catalogue-service

# Debug Python app
docker exec -it catalogue-service python -c "import app; print('App imports successfully')"

# Check environment variables
docker exec -it catalogue-service env
```

## Development Workflow

### 1. Code Changes
```sh
# Make changes to Python files
# Test locally
python app.py

# Test with different data sources
DATA_SOURCE=json python app.py
DATA_SOURCE=db python app.py
```

### 2. Docker Development
```sh
# Build and run with volume mounting
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  -v $(pwd):/app \
  -w /app \
  --env-file .env \
  python:3.11 \
  python app.py
```

### 3. Database Setup
```sh
# Create database (if using local PostgreSQL)
createdb catalogue
psql -d catalogue -c "CREATE TABLE IF NOT EXISTS products (...);"

# Or use Docker PostgreSQL
docker run -d \
  --name postgres-db \
  -e POSTGRES_DB=catalogue \
  -e POSTGRES_USER=devops \
  -e POSTGRES_PASSWORD=devops \
  postgres:15
```

## Deployment

### Production Considerations
- Use environment variables for configuration
- Implement proper logging
- Add health checks
- Use Docker secrets for database credentials
- Configure proper resource limits
- Implement graceful shutdown
- Use reverse proxy (nginx)
- Set up SSL/TLS

### Production Docker Run
```sh
docker run -d \
  --name catalogue-service \
  --restart unless-stopped \
  -p 5000:5000 \
  --env-file .env \
  --memory="512m" \
  --cpus="1.0" \
  crafty-catalogue-service
```

### Docker Compose Production
```yaml
version: '3.8'
services:
  catalogue-service:
    build: .
    ports:
      - "5000:5000"
    env_file:
      - .env
    environment:
      - DATA_SOURCE=db
    depends_on:
      - postgres-db
    networks:
      - crafty-network

  postgres-db:
    image: postgres:15
    environment:
      - POSTGRES_DB=catalogue
      - POSTGRES_USER=devops
      - POSTGRES_PASSWORD=devops
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - crafty-network

volumes:
  postgres_data:

networks:
  crafty-network:
    driver: bridge
```

## Data Management

### JSON Mode
- Data stored in `products.json`
- Hot-reloadable (changes take effect immediately)
- No database setup required
- Suitable for development and small-scale deployments

### Database Mode
- Data stored in PostgreSQL
- Supports concurrent access
- Data persistence across container restarts
- Suitable for production deployments
- Requires database setup and migration

## Service Dependencies

### Dependent Services
- **Voting Service**: Uses catalogue API to sync origami data
- **Recommendation Service**: Uses catalogue API to get product data
- **Frontend Service**: Proxies requests to catalogue API

### Service Communication
- Provides REST API for product data
- Supports both JSON and database backends
- Implements error handling and graceful degradation

## Performance Optimization

### JSON Mode Optimizations
- File caching for improved performance
- In-memory data loading
- Fast read operations

### Database Mode Optimizations
- Connection pooling
- Query optimization
- Database indexing
- Prepared statements

## Security Considerations

### Basic Security
- Input validation and sanitization
- SQL injection prevention (database mode)
- CORS configuration
- Rate limiting (can be implemented)
- Secure headers implementation

### Docker Security
- Non-root user execution
- Minimal base image
- No sensitive data in images
- Regular security updates
- Network isolation

## Contributing

### Development Setup
1. Fork the repository
2. Clone your fork
3. Create feature branch
4. Set up environment (.env file)
5. Install dependencies
6. Make changes
7. Test locally
8. Submit pull request

### Code Standards
- Follow PEP 8 style guide
- Write tests for new features
- Update documentation
- Use type hints
- Handle errors gracefully

## Notes
- The service supports both JSON file and PostgreSQL database backends
- JSON mode is suitable for development and small deployments
- Database mode is recommended for production
- The service provides REST API for product management
- Docker networking enables seamless inter-service communication
- Environment-based configuration for different deployment scenarios

