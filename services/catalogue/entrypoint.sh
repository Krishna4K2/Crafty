#!/bin/sh
set -e

if [ "$DATA_SOURCE" = "db" ]; then
  echo "Running in POSTGRES mode..."

  until PGPASSWORD="$DB_PASSWORD" pg_isready -h "$DB_HOST" -U "$DB_USER"; do
    echo "Waiting for PostgreSQL at $DB_HOST..."
    sleep 2
  done

  echo "PostgreSQL is ready. Initializing DB..."
  python db.create.py
else
  echo "Running in JSON mode... Skipping Postgres init."
fi

echo "Starting Flask app with Gunicorn..."
exec gunicorn app:app --bind 0.0.0.0:5000
