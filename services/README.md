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

## 🚀 Quick Start - All Services

### Prerequisites
- Docker & Docker Compose installed
- At least 4GB RAM available
- Ports 3000, 5000, 8080, 8086, 5432 available

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
# Run with JSON data source (default)
docker-compose up -d

# Or run with PostgreSQL
DATA_SOURCE=db docker-compose up -d
```

### Voting Service
```bash
cd voting
# Run with H2 in-memory database
docker-compose up -d

# Or run with MongoDB
SPRING_PROFILES_ACTIVE=mongo docker-compose up -d
```

### Recommendation Service
```bash
cd recommendation
# Run standalone (needs catalogue service)
docker-compose up -d

# Or with catalogue service
docker-compose --profile with-catalogue up -d
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
- **MongoDB Mode**: Data persisted in `mongo_data` volume

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
SPRING_PROFILES_ACTIVE=default  # default or mongo
CATALOGUE_SERVICE_URL=http://catalogue:5000/api/products
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
- Databases use default credentials (change for production)
- No sensitive data is exposed in logs
- All services communicate over internal Docker network

## 📚 Additional Resources

- [Individual Service READMEs](./frontend/README.md)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Crafty Architecture](../docs/crafty.md)

---

**Happy Crafting! 🎨**</content>
<parameter name="filePath">C:\Users\saikr\GithubRepos\Crafty\services\README.md
