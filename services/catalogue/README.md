
# Crafty Catalogue Service - Quick Start Guide

## 1. Choose Your Data Source

You can run the app using either a local JSON file or a PostgreSQL database.

Configuration is handled via the .env file.
```sh
  cp .env.example .env # create .env file
```

Here’s a sample .env file you can use:
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
  * Update values in .env as needed (especially DB credentials).
  * Never commit real secrets in .env for production projects. For this learning/demo project, we are keeping .env in GitHub.


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

---

## 2. Local Python Environment Setup

```sh
python -m venv venv         # Create virtual environment
venv\Scripts\activate       # Activate (Windows)
source venv/bin/activate    # Activate (Linux/Mac)
deactivate                  # Deactivate
```

## 3. Install Dependencies & Build

```sh
pip install -r requirements.txt
```

## 4. Run the App in local

- **Local (Windows/Linux/Mac):**
  ```sh
  python app.py
  ```
- **Production or Linux:**
  ```sh
  gunicorn app:app --bind 0.0.0.0:5000
  ```

## 5. Run only APP in Docker container
  ```sh
  docker build -t my-python-app . # Build the image
  docker run -d -p 5000:5000 --env-file .env  my-python-app # Run container with image
  ```

## 6. Run the APP and PostgrSQL with Docker Compose

```sh
docker compose --env-file .env up --build
```
- The app will be available at [http://localhost:5000](http://localhost:5000)
- PostgreSQL will run in a separate container and persist data in a Docker volume.

To stop and remove containers:
```sh
docker-compose down
```

