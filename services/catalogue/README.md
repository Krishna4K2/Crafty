# Crafty Catalogue Service - Complete Setup Guide

## Overview
The Catalogue Service is a Python Flask microservice that manages the origami product catalog. It supports both JSON file-based storage and PostgreSQL database storage, making it flexible for different deployment scenarios.

## Prerequisites
- **Python 3.8+** installed
- **pip** (Python package manager)
- **PostgreSQL** (optional, only if using database mode)
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for cloning the repository)

## Quick Start with Docker Compose (Recommended)

### 1. Environment Setup
```bash
# Navigate to catalogue service directory
cd services/catalogue

# Copy environment template
cp .env.example .env

# Edit .env file with your preferred settings
# See Environment Configuration section below
```

### 2. Choose Your Data Source

#### Option A: Run with JSON File (Default)
```bash
# JSON mode is the default - no additional configuration needed
docker-compose up -d

# Access the service
curl http://localhost:5000/api/products
```

#### Option B: Run with PostgreSQL Database
```bash
# Edit .env file and set DATA_SOURCE=db
# DATA_SOURCE=db

# Start services with database
docker-compose up -d

# The database will be automatically initialized with data from products.json
```

### 3. Verify Service is Running
```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs -f catalogue

# Test API endpoint
curl http://localhost:5000/api/products
```

## Environment Configuration

### .env File Setup
The service uses environment variables for configuration. Docker Compose automatically loads `.env` files from the current directory.

```bash
# Copy the example file
cp .env.example .env
```

### Configuration Options

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DATA_SOURCE` | Data storage type: `json` or `db` | `json` | Yes |
| `DB_HOST` | PostgreSQL hostname | `catalogue-db` | Only for DB mode |
| `DB_NAME` | PostgreSQL database name | `catalogue` | Only for DB mode |
| `DB_USER` | PostgreSQL username | `crafty` | Only for DB mode |
| `DB_PASSWORD` | PostgreSQL password | `crafty` | Only for DB mode |
| `CATALOGUE_PORT` | Service port | `5000` | No |
| `POSTGRES_PORT` | Database port | `5432` | No |
| `APP_VERSION` | Application version | `1.0.0` | No |

### Example .env Configurations

#### For JSON Mode (File-based storage)
```bash
APP_VERSION=1.0.0
DATA_SOURCE=json
CATALOGUE_PORT=5000
```

#### For PostgreSQL Mode (Database storage)
```bash
APP_VERSION=1.0.0
DATA_SOURCE=db
DB_HOST=catalogue-db
DB_NAME=catalogue
DB_USER=crafty
DB_PASSWORD=crafty
CATALOGUE_PORT=5000
POSTGRES_PORT=5432
```

## Running the Service

### Method 1: Docker Compose (Recommended)

#### Start Services
```bash
# Start in detached mode
docker-compose up -d

# Start with specific environment file
docker-compose --env-file .env up -d

# Start and follow logs
docker-compose up
```

#### Stop Services
```bash
# Stop services
docker-compose down

# Stop and remove volumes (WARNING: deletes database data)
docker-compose down -v
```

#### View Logs and Status
```bash
# View all logs
docker-compose logs

# Follow logs for specific service
docker-compose logs -f catalogue

# Check service status
docker-compose ps
```

### Method 2: Individual Docker Containers

#### Build the Image
```bash
docker build -t crafty-catalogue .
```

#### Run with JSON Mode
```bash
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  -e DATA_SOURCE=json \
  crafty-catalogue
```

#### Run with PostgreSQL Mode
```bash
# Start PostgreSQL first
docker run -d \
  --name postgres-db \
  -p 5432:5432 \
  -e POSTGRES_DB=catalogue \
  -e POSTGRES_USER=crafty \
  -e POSTGRES_PASSWORD=crafty \
  postgres:15

# Start catalogue service
docker run -d \
  --name catalogue-service \
  -p 5000:5000 \
  --link postgres-db:postgres \
  -e DATA_SOURCE=db \
  -e DB_HOST=postgres \
  -e DB_NAME=catalogue \
  -e DB_USER=crafty \
  -e DB_PASSWORD=crafty \
  crafty-catalogue
```

### Method 3: Local Development

#### Python Environment Setup
```bash
# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

#### Run Locally
```bash
# With JSON mode
DATA_SOURCE=json python app.py

# With PostgreSQL mode
DATA_SOURCE=db python app.py
```

## Data Source Details

### JSON Mode
- **Storage**: File-based using `products.json`
- **Pros**: Simple, no database required, fast startup
- **Cons**: No concurrent write support, file-based persistence
- **Use Case**: Development, testing, single-instance deployments

### PostgreSQL Mode
- **Storage**: Relational database with persistent data
- **Pros**: Concurrent access, ACID transactions, advanced queries
- **Cons**: Requires database setup, slightly slower startup
- **Use Case**: Production, multi-instance deployments, data persistence

### Switching Between Modes
```bash
# Switch to JSON mode
echo "DATA_SOURCE=json" >> .env
docker-compose up -d

# Switch to database mode
echo "DATA_SOURCE=db" >> .env
docker-compose up -d
```

## API Endpoints

### Get All Products
```bash
curl http://localhost:5000/api/products
```

### Get Specific Product
```bash
curl http://localhost:5000/api/products/1
```

### Web Interface
```bash
# Open in browser
open http://localhost:5000
```

## Database Management

### Database Initialization
When running in PostgreSQL mode, the database is automatically initialized using `db.create.py` which:
1. Creates the `products` table with proper schema
2. Imports data from `products.json`
3. Handles schema updates safely

### Database Schema
```sql
CREATE TABLE products (
    id INTEGER PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100),
    difficulty VARCHAR(50),
    tags JSONB,
    short_description VARCHAR(300),
    description VARCHAR(1000),
    image_url VARCHAR(300),
    created_at DATE
);
```

### Manual Database Operations
```bash
# Connect to database
docker-compose exec postgres psql -U crafty -d catalogue

# Backup database
docker-compose exec postgres pg_dump -U crafty catalogue > backup.sql

# Restore database
docker-compose exec -T postgres psql -U crafty catalogue < backup.sql
```

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose logs catalogue

# Verify environment variables
docker-compose exec catalogue env

# Check container health
docker-compose ps
```

### Database Connection Issues
```bash
# Check database logs
docker-compose logs postgres

# Verify database is ready
docker-compose exec postgres pg_isready -U crafty -d catalogue

# Test database connection
docker-compose exec catalogue python -c "import psycopg2; print('DB connection OK')"
```

### Port Conflicts
```bash
# Check what's using the port
lsof -i :5000

# Use different ports in .env
CATALOGUE_PORT=5001
POSTGRES_PORT=5433
```

### Permission Issues
```bash
# Fix file permissions
chmod +x entrypoint.sh

# Rebuild containers
docker-compose build --no-cache
```

## Development

### Project Structure
```
catalogue/
├── app.py              # Main Flask application
├── db.create.py        # Database initialization script
├── products.json       # Product data for JSON mode
├── requirements.txt    # Python dependencies
├── Dockerfile         # Docker image definition
├── docker-compose.yml # Docker Compose configuration
├── entrypoint.sh      # Container entrypoint script
├── .env.example       # Environment variables template
└── static/            # Static assets (CSS, images)
    └── templates/     # HTML templates
```

### Adding New Products
- **JSON Mode**: Edit `products.json` directly
- **Database Mode**: Insert records into PostgreSQL `products` table

### Code Changes
```bash
# Rebuild after code changes
docker-compose build

# Restart services
docker-compose up -d
```

## Production Deployment

### Security Considerations
- Change default database credentials in production
- Use Docker secrets for sensitive environment variables
- Run containers as non-root user
- Use HTTPS in production
- Implement proper logging and monitoring

### Performance Optimization
```bash
# Use production WSGI server
gunicorn app:app --bind 0.0.0.0:5000 --workers 4

# Enable connection pooling for database
# Configure PostgreSQL connection limits
```

### Backup Strategy
```bash
# Regular database backups
docker-compose exec postgres pg_dump -U crafty catalogue > backup_$(date +%Y%m%d).sql

# Volume backups
docker run --rm -v catalogue_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

## Support

### Common Issues
1. **Database connection fails**: Check DB_HOST, credentials, and network connectivity
2. **Service won't start**: Verify environment variables and dependencies
3. **Port already in use**: Change ports in .env file or stop conflicting services
4. **Permission denied**: Fix file permissions and user contexts

### Logs and Debugging
```bash
# Enable debug mode
docker-compose exec catalogue python -c "import logging; logging.basicConfig(level=logging.DEBUG)"

# Check Flask logs
docker-compose logs -f catalogue

# Access container shell
docker-compose exec catalogue bash
```

---

**Note**: This service is designed to work as part of the larger Crafty microservices architecture. For full system deployment, see the main project README.
