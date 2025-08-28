#!/bin/sh
set -e

echo "Starting Recommendation Service..."
echo "Catalogue API URL: $CATALOGUE_API_URL"
echo "Port: $PORT"

# Wait for catalogue service if URL contains localhost (for development)
if echo "$CATALOGUE_API_URL" | grep -q "localhost"; then
    echo "Running in development mode - checking catalogue service availability..."

    # Simple check if catalogue service is responding
    if curl -f -s "$CATALOGUE_API_URL" > /dev/null 2>&1; then
        echo "Catalogue service is available"
    else
        echo "Warning: Catalogue service may not be available at $CATALOGUE_API_URL"
        echo "The service will still start but may return errors for origami requests"
    fi
else
    echo "Running in production mode with external catalogue service"
fi

echo "Starting Go application..."
exec ./recommendation
