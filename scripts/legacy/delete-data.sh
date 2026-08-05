#!/bin/sh

# Full reset of the local PostgreSQL environment:
#   1. Stop and remove the container
#   2. Delete the persisted volume (all database files)
#   3. Start a fresh container via run-postgres.sh
#
# Use this when you want a clean Podman volume, not just a fresh Hibernate schema.
# For normal learning runs, starting the Spring app alone is enough (create-drop + data.sql).

set -e

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

CONTAINER_NAME="gymcrm-postgres"
VOLUME_NAME="gymcrm_postgres_data"

echo "Stopping and removing container '$CONTAINER_NAME'..."
podman rm -f "$CONTAINER_NAME" 2>/dev/null || true

echo "Removing volume '$VOLUME_NAME'..."
podman volume rm "$VOLUME_NAME" 2>/dev/null || true

echo "Starting a fresh PostgreSQL container..."
"$SCRIPT_DIR/run-postgres.sh"
