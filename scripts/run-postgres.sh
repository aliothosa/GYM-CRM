#!/bin/sh

set -e

CONTAINER_NAME="gymcrm-postgres"
VOLUME_NAME="gymcrm_postgres_data"
DB_NAME="gymcrm"
DB_USER="gymcrm_user"
DB_PASSWORD="gymcrm_pass"
DB_PORT="5432"

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
  -p "$DB_PORT:5432" \
  -v "$VOLUME_NAME:/var/lib/postgresql/data" \
  docker.io/postgres:16-alpine

echo "Container '$CONTAINER_NAME' created and running in background"