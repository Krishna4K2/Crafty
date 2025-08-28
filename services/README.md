# Crafty Microservices - Docker Compose Setup

This directory contains Docker Compose configurations for running the complete Crafty microservices architecture.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Catalogue     │    │   Voting        │
│   Node.js       │    │   Python/Flask  │    │   Java/Spring   │
│   Port: 3000    │    │   Port: 5000    │    │   Port: 8086    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Recommendation  │
                    │ Go              │
                    │ Port: 8080      │
                    └─────────────────┘
```

## � Environment Setup

### Prerequisites
- Docker & Docker Compose installed
- At least 4GB RAM available
- Ports 3000, 5000, 8080, 8086, 5432, 27017 available

### Setup Environment Variables
```bash
# Copy the environment template
cp .env.example .env

# Edit the .env file with your desired configuration
# WARNING: DO NOT commit .env file to GitHub - it may contain sensitive data
```

The `.env` file contains all configuration options including:
- Database settings (PostgreSQL, MongoDB)
- Service URLs and ports
- Environment modes (development/production)
- Database credentials

## 🚀 Quick Start - All Services

### Start All Services
```bash
# From services/ directory
docker-compose up -d

# View logs
docker-compose logs -f

# Check service health
docker-compose ps
```

### Stop All Services
```bash
docker-compose down
```

### View Service URLs
- **Frontend**: http://localhost:3000
- **Catalogue API**: http://localhost:5000
- **Voting API**: http://localhost:8086
- **Recommendation API**: http://localhost:8080
- **Catalogue Database**: localhost:5432
- **Voting Database (MongoDB)**: localhost:27017

## 🔧 Database Configuration Options

### Catalogue Service
```bash
# Edit .env file to configure data source
# DATA_SOURCE=json (default) or DATA_SOURCE=db (PostgreSQL)
```

### Voting Service
```bash
# Edit .env file to configure database
# SPRING_PROFILES_ACTIVE=default (H2) or SPRING_PROFILES_ACTIVE=mongodb (MongoDB)
```

### Combined Configuration
```bash
# Lightweight setup (H2 + JSON) - data lost on restart
# Set in .env: DATA_SOURCE=json, SPRING_PROFILES_ACTIVE=default
docker-compose up -d

# Persistent setup (MongoDB + PostgreSQL) - data survives restarts
# Set in .env: DATA_SOURCE=db, SPRING_PROFILES_ACTIVE=mongodb
docker-compose up -d

# Mixed setup (MongoDB voting + JSON catalogue) - catalogue data lost, voting data persistent
# Set in .env: DATA_SOURCE=json, SPRING_PROFILES_ACTIVE=mongodb
docker-compose up -d
```

## 🔧 Individual Service Setup

Each service can be run independently with its own Docker Compose configuration:

### Frontend Service
```bash
cd frontend
docker-compose up -d
# Or with full stack for testing
docker-compose --profile full-stack up -d
```

### Catalogue Service
```bash
cd catalogue
# Configure DATA_SOURCE in .env file (json or db)
docker-compose up -d
```

### Voting Service
```bash
cd voting
# Configure SPRING_PROFILES_ACTIVE in .env file (default or mongodb)
docker-compose up -d
```

### Recommendation Service
```bash
cd recommendation
# Configure CATALOGUE_SERVICE_URL_RECOMMENDATION in .env file if needed
docker-compose up -d
```

## 📊 Service Dependencies

| Service | Dependencies | Database | Port |
|---------|-------------|----------|------|
| Frontend | Catalogue, Voting, Recommendation | None | 3000 |
| Catalogue | None | PostgreSQL (optional) | 5000 |
| Voting | Catalogue | H2/MongoDB | 8086 |
| Recommendation | Catalogue | None | 8080 |

## 🔍 Health Checks

All services include health checks that verify:
- Service is responding to HTTP requests
- Database connections are working
- Dependent services are available

```bash
# Check all service health
docker-compose ps

# View specific service logs
docker-compose logs catalogue
```

## 🗃️ Data Persistence

### Catalogue Service
- **JSON Mode**: Data stored in container (`products.json`)
- **PostgreSQL Mode**: Data persisted in `catalogue_data` volume

### Voting Service
- **H2 Mode**: Data stored in memory (lost on restart)
- **MongoDB Mode**: Data persisted in `voting_data` volume

## 🔧 Environment Variables

### Global Configuration
```bash
# Service URLs (used by frontend)
PRODUCTS_API_BASE_URI=http://catalogue:5000
RECOMMENDATION_BASE_URI=http://recommendation:8080
VOTING_BASE_URI=http://voting:8086
```

### Catalogue Service
```bash
DATA_SOURCE=json          # json or db
DB_HOST=catalogue-db      # PostgreSQL host
DB_NAME=catalogue         # Database name
DB_USER=user             # Database user
DB_PASSWORD=password     # Database password
```

### Voting Service
```bash
SPRING_PROFILES_ACTIVE=default  # default (H2) or mongodb (MongoDB)
CATALOGUE_SERVICE_URL=http://catalogue:5000/api/products
```

#### Database Configuration Options

**H2 Database (Default):**
```bash
SPRING_PROFILES_ACTIVE=default
# Uses in-memory H2 database - data is lost on restart
```

**MongoDB Database:**
```bash
SPRING_PROFILES_ACTIVE=mongodb
# Uses MongoDB for persistent data storage
```

## 🐛 Troubleshooting

### Common Issues

1. **Port conflicts**
   ```bash
   # Find what's using the port
   netstat -tulpn | grep :3000

   # Stop conflicting service or use different ports
   docker-compose down
   ```

2. **Service startup failures**
   ```bash
   # Check service logs
   docker-compose logs <service-name>

   # Restart specific service
   docker-compose restart <service-name>
   ```

3. **Database connection issues**
   ```bash
   # Check database logs
   docker-compose logs catalogue-db

   # Verify database is healthy
   docker-compose exec catalogue-db pg_isready -U user -d catalogue
   ```

### Reset Everything
```bash
# Stop all services and remove volumes
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Clean rebuild
docker-compose build --no-cache
```

## 📈 Monitoring

### Service Status
```bash
# All services
docker-compose ps

# Service health
docker-compose exec frontend curl -f http://localhost:3000
```

### Resource Usage
```bash
# Container resource usage
docker stats

# Specific service logs
docker-compose logs -f frontend
```

## 🔒 Security Notes

- Services run with health checks and restart policies
- Databases use default credentials (change for production in .env file)
- **DO NOT commit .env file to GitHub** - it may contain sensitive data like passwords
- Use strong passwords in production environments
- No sensitive data is exposed in logs
- All services communicate over internal Docker network

## 📚 Additional Resources

- [Individual Service READMEs](./frontend/README.md)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Crafty Architecture](../docs/crafty.md)

---
