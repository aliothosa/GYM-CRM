#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_ROOT="$(cd -- "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${REPOSITORY_ROOT}/.env"
STARTUP_TIMEOUT_SECONDS="${STARTUP_TIMEOUT_SECONDS:-120}"

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "Required command '$1' is not installed." >&2
    exit 1
  }
}

select_compose_command() {
  if command -v docker >/dev/null 2>&1 \
      && docker info >/dev/null 2>&1 \
      && docker compose version >/dev/null 2>&1; then
    COMPOSE_COMMAND=(docker compose)
    return
  fi

  if command -v docker-compose >/dev/null 2>&1 \
      && docker info >/dev/null 2>&1 \
      && docker-compose version >/dev/null 2>&1; then
    COMPOSE_COMMAND=(docker-compose)
    return
  fi

  if command -v podman >/dev/null 2>&1 \
      && podman info >/dev/null 2>&1 \
      && podman compose version >/dev/null 2>&1; then
    COMPOSE_COMMAND=(podman compose)
    return
  fi

  if command -v podman-compose >/dev/null 2>&1 \
      && podman info >/dev/null 2>&1; then
    COMPOSE_COMMAND=(podman-compose)
    return
  fi

  echo "Neither a working Docker Compose nor Podman Compose installation was found." >&2
  exit 1
}

http_status() {
  curl --silent --show-error --output /dev/null --write-out '%{http_code}' \
    --connect-timeout 3 --max-time 5 "$1" || true
}

wait_for_services() {
  local deadline=$((SECONDS + STARTUP_TIMEOUT_SECONDS)) api_status workload_status

  while (( SECONDS <= deadline )); do
    api_status="$(http_status http://localhost:8080/actuator/health)"
    workload_status="$(http_status http://localhost:8081/actuator/health)"

    if [[ ( "$api_status" == "200" || "$api_status" == "401" ) && "$workload_status" == "200" ]]; then
      echo "Stack is ready for testing: gym-crm-api=$api_status trainer-workload-service=$workload_status"
      return
    fi

    sleep 2
  done

  echo "Timed out after ${STARTUP_TIMEOUT_SECONDS}s waiting for the microservices." >&2
  "${COMPOSE_COMMAND[@]}" ps >&2 || true
  exit 1
}

require_command curl
[[ -f "$ENV_FILE" ]] || { echo "Missing environment file: $ENV_FILE" >&2; exit 1; }
[[ "$STARTUP_TIMEOUT_SECONDS" =~ ^[1-9][0-9]*$ ]] || {
  echo "STARTUP_TIMEOUT_SECONDS must be a positive integer." >&2
  exit 1
}

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

: "${POSTGRES_PASSWORD:?POSTGRES_PASSWORD must be set in .env}"
: "${GYMCRM_JWT_SECRET:?GYMCRM_JWT_SECRET must be set in .env}"

select_compose_command
cd "$REPOSITORY_ROOT"

echo "Building and starting the compose stack with ${COMPOSE_COMMAND[*]}..."
"${COMPOSE_COMMAND[@]}" up --build --detach
wait_for_services
