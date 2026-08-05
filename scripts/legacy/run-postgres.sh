#!/bin/sh

set -e

CONTAINER_NAME="gymcrm-postgres"
VOLUME_NAME="gymcrm_postgres_data"
DB_NAME="gymcrm"
DB_USER="gymcrm_user"
DB_PASSWORD="gymcrm_pass"
DB_PORT="5432"
IMAGE="docker.io/postgres:16-alpine"

echo "Creating volume if it does not exist..."
podman volume exists "$VOLUME_NAME" || podman volume create "$VOLUME_NAME"

echo "Removing existing container if it exists..."
podman rm -f "$CONTAINER_NAME" 2>/dev/null || true

echo "Starting PostgreSQL container..."
podman run -d \
  --name "$CONTAINER_NAME" \
  -e POSTGRES_DB="$DB_NAME" \
  -e POSTGRES_USER="$DB_USER" \
  -e POSTGRES_PASSWORD="$DB_PASSWORD" \
  -p "${DB_PORT}:5432" \
  -v "${VOLUME_NAME}:/var/lib/postgresql/data:Z" \
  "$IMAGE"

echo "Waiting for PostgreSQL to accept connections..."
attempt=0
until podman exec "$CONTAINER_NAME" pg_isready -U "$DB_USER" -d "$DB_NAME" >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  if [ "$attempt" -ge 30 ]; then
    echo "PostgreSQL did not become ready in time." >&2
    exit 1
  fi
  sleep 1
done

echo "PostgreSQL is ready."
echo "  Container : $CONTAINER_NAME"
echo "  Database  : $DB_NAME"
echo "  User      : $DB_USER"
echo "  Port      : $DB_PORT"
echo ""
echo "Credentials match src/main/resources/application.properties"
