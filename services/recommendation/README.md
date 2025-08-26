
# How to Build & Run Recommendation App

## Local Build & Run

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

## Docker Build & Run

1. **Build Docker Image:**
   ```sh
   docker build -t recommendation-app .
   ```
2. **Run Docker Container:**
   ```sh
   docker run -d -p 8080:8080 -e CATALOGUE_API_URL="http://<catalogue-host>:5000/api/products" recommendation-app
   ```

## Service Dependency

**Note:** This recommendation service depends on the catalogue service. It fetches product data from the catalogue service via its API (`/api/products`).

- Make sure the catalogue service is running and accessible before starting the recommendation service.
- By default, it expects the catalogue API at `http://localhost:5000/api/products`. You can override this using the `CATALOGUE_API_URL` environment variable.

## Environment Variables

- `CATALOGUE_API_URL` (optional): Set this to the catalogue service API endpoint if not running on localhost:5000.

## Endpoints

- `/` : Home page
- `/api/origami-of-the-day` : Get a random origami product from catalogue
- `/api/recommendation-status` : Service status
 