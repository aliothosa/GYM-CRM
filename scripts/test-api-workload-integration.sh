#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_ROOT="$(cd -- "${SCRIPT_DIR}/.." && pwd)"
API_URL="${GYMCRM_API_URL:-http://localhost:8080}"
WORKLOAD_URL="${WORKLOAD_SERVICE_URL:-http://localhost:8081}"
TIMEOUT_SECONDS="${INTEGRATION_TIMEOUT_SECONDS:-15}"
API_URL="${API_URL%/}"
WORKLOAD_URL="${WORKLOAD_URL%/}"

TRAINER_USERNAME="John.Doe"
TRAINEE_USERNAME="Emily.Davis"
YEAR=2030
MONTH=7
DATE="2030-07-21"
FIRST_DURATION=60
SECOND_DURATION=90
TOKEN=""
FIRST_ID=""
SECOND_ID=""
BASELINE=0

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "Required command '$1' is not installed. ${2}" >&2
    exit 1
  }
}

sanitize_body() {
  local body="$1"
  body="${body//$'\n'/ }"
  printf '%s' "${body:0:300}"
}

request() {
  local method="$1" url="$2" data="${3:-}"
  local response_file
  response_file="$(mktemp)"
  local curl_args=(-sS --connect-timeout 5 --max-time 10 -o "$response_file" -w '%{http_code}' -X "$method" -H 'Accept: application/json')
  if [[ -n "$TOKEN" ]]; then curl_args+=(-H "Authorization: Bearer $TOKEN"); fi
  if [[ -n "$data" ]]; then curl_args+=(-H 'Content-Type: application/json' --data "$data"); fi
  if ! HTTP_STATUS="$(curl "${curl_args[@]}" "$url")"; then
    rm -f "$response_file"
    echo "Request failed: ${method} ${url}. Ensure the application stack is running." >&2
    return 1
  fi
  HTTP_BODY="$(<"$response_file")"
  rm -f "$response_file"
}

expect_status() {
  local expected="$1" action="$2"
  if [[ "$HTTP_STATUS" != "$expected" ]]; then
    echo "$action failed: HTTP $HTTP_STATUS (expected $expected). Response: $(sanitize_body "$HTTP_BODY")" >&2
    return 1
  fi
}

check_ready() {
  local name="$1" url="$2" allow_unauthorized="${3:-false}"
  TOKEN=""
  request GET "$url/actuator/health" || {
    echo "$name is not reachable at $url. Start the application stack before running this test." >&2
    exit 1
  }
  if [[ "$HTTP_STATUS" != "200" && !( "$allow_unauthorized" == "true" && "$HTTP_STATUS" == "401" ) ]]; then
    echo "$name is not reachable at $url (health returned HTTP $HTTP_STATUS). Start the application stack before running this test." >&2
    exit 1
  fi
}

read_workload() {
  request GET "$WORKLOAD_URL/workloads/$TRAINER_USERNAME?year=$YEAR&month=$MONTH"
  if [[ "$HTTP_STATUS" == "404" ]]; then
    ACTUAL_WORKLOAD=0
    return 0
  fi
  expect_status 200 "Reading trainer workload"
  local username found_year found_month duration
  username="$(jq -r '.trainerUsername // empty' <<<"$HTTP_BODY")"
  found_year="$(jq -r '.years[]? | select(.year == '"$YEAR"') | .year' <<<"$HTTP_BODY")"
  found_month="$(jq -r '.years[]? | select(.year == '"$YEAR"') | .months[]? | select(.month == '"$MONTH"') | .month' <<<"$HTTP_BODY")"
  duration="$(jq -r '.years[]? | select(.year == '"$YEAR"') | .months[]? | select(.month == '"$MONTH"') | .trainingSummaryDurationMinutes' <<<"$HTTP_BODY")"
  [[ "$username" == "$TRAINER_USERNAME" && "$found_year" == "$YEAR" && "$found_month" == "$MONTH" && "$duration" =~ ^[0-9]+$ ]] || {
    echo "Workload response did not contain the requested trainer/year/month. Response: $(sanitize_body "$HTTP_BODY")" >&2
    return 1
  }
  ACTUAL_WORKLOAD="$duration"
}

wait_for_workload() {
  local expected="$1" label="$2" deadline=$((SECONDS + TIMEOUT_SECONDS))
  while (( SECONDS <= deadline )); do
    read_workload
    if [[ "$ACTUAL_WORKLOAD" == "$expected" ]]; then return 0; fi
    sleep 1
  done
  echo "$label workload assertion failed after ${TIMEOUT_SECONDS}s: expected $expected minutes, last observed $ACTUAL_WORKLOAD minutes." >&2
  return 1
}

find_training_id() {
  local training_name="$1" duration="$2" deadline=$((SECONDS + TIMEOUT_SECONDS))
  while (( SECONDS <= deadline )); do
    request GET "$API_URL/trainers/$TRAINER_USERNAME/trainings-simple"
    expect_status 200 "Listing trainer trainings"
    local id
    id="$(jq -r --arg name "$training_name" --arg date "$DATE" --arg trainee "$TRAINEE_USERNAME" --argjson duration "$duration" '
      .[] | select(
        .embeddedResponse.name == $name
        and .embeddedResponse.date == $date
        and (.embeddedResponse.traineeName | contains($trainee))
        and .embeddedResponse.duration == $duration
      ) | .id' <<<"$HTTP_BODY" | head -n 1)"
    if [[ "$id" =~ ^[0-9]+$ ]]; then printf '%s' "$id"; return 0; fi
    sleep 1
  done
  echo "Could not locate the newly created training '$training_name' within ${TIMEOUT_SECONDS}s." >&2
  return 1
}

cleanup() {
  local original_status=$?
  trap - EXIT
  set +e
  local cleanup_failed=0 id
  for id in "$FIRST_ID" "$SECOND_ID"; do
    [[ -z "$id" ]] && continue
    request DELETE "$API_URL/trainers/$TRAINER_USERNAME/trainings/$id"
    if [[ "$HTTP_STATUS" != "204" && "$HTTP_STATUS" != "404" ]]; then
      echo "Cleanup failed for training $id: HTTP $HTTP_STATUS. Response: $(sanitize_body "$HTTP_BODY")" >&2
      cleanup_failed=1
    fi
  done
  if [[ -n "$TOKEN" && "$cleanup_failed" == 0 ]]; then
    if wait_for_workload "$BASELINE" "Cleanup"; then
      echo "Cleanup restored workload baseline: $BASELINE minutes"
    else
      cleanup_failed=1
    fi
  fi
  [[ "$original_status" != 0 ]] && exit "$original_status"
  [[ "$cleanup_failed" != 0 ]] && exit 1
}
trap cleanup EXIT

require_command curl "Install curl and retry."
require_command jq "Install jq (for example, 'sudo apt install jq') and retry."
[[ "$TIMEOUT_SECONDS" =~ ^[1-9][0-9]*$ ]] || { echo "INTEGRATION_TIMEOUT_SECONDS must be a positive integer." >&2; exit 1; }

echo "[1/7] Checking service availability..."
check_ready "gym-crm-api" "$API_URL" true
check_ready "trainer-workload-service" "$WORKLOAD_URL"

echo "[2/7] Authenticating as Emily.Davis..."
request POST "$API_URL/auth/login" '{"username":"Emily.Davis","password":"pass123"}'
expect_status 200 "Authentication"
TOKEN="$(jq -r '.accessToken // empty' <<<"$HTTP_BODY")"
[[ -n "$TOKEN" ]] || { echo "Authentication response did not contain a non-empty accessToken." >&2; exit 1; }
echo "Authentication successful."

echo "[3/7] Reading baseline workload..."
read_workload
BASELINE="$ACTUAL_WORKLOAD"
echo "Baseline workload: $BASELINE minutes"

suffix="$(date +%s)"
FIRST_NAME="API workload integration ${suffix}-A"
SECOND_NAME="API workload integration ${suffix}-B"
echo "[4/7] Creating two trainings..."
request POST "$API_URL/trainings" "$(jq -nc --arg trainee "$TRAINEE_USERNAME" --arg trainer "$TRAINER_USERNAME" --arg name "$FIRST_NAME" --arg date "$DATE" --argjson duration "$FIRST_DURATION" '{traineeUsername:$trainee,trainerUsername:$trainer,trainingName:$name,date:$date,durationInMinutes:$duration}')"
expect_status 200 "Creating first training"
FIRST_ID="$(find_training_id "$FIRST_NAME" "$FIRST_DURATION")"
request POST "$API_URL/trainings" "$(jq -nc --arg trainee "$TRAINEE_USERNAME" --arg trainer "$TRAINER_USERNAME" --arg name "$SECOND_NAME" --arg date "$DATE" --argjson duration "$SECOND_DURATION" '{traineeUsername:$trainee,trainerUsername:$trainer,trainingName:$name,date:$date,durationInMinutes:$duration}')"
expect_status 200 "Creating second training"
SECOND_ID="$(find_training_id "$SECOND_NAME" "$SECOND_DURATION")"

expected_after_add=$((BASELINE + FIRST_DURATION + SECOND_DURATION))
echo "[5/7] Verifying workload after ADD events..."
wait_for_workload "$expected_after_add" "ADD"
echo "Expected after ADD: $expected_after_add minutes"
echo "Actual after ADD:   $ACTUAL_WORKLOAD minutes"

echo "[6/7] Deleting training $FIRST_ID..."
request DELETE "$API_URL/trainers/$TRAINER_USERNAME/trainings/$FIRST_ID"
expect_status 204 "Deleting first training"
FIRST_ID=""

expected_after_delete=$((BASELINE + SECOND_DURATION))
echo "[7/7] Verifying workload after DELETE event..."
wait_for_workload "$expected_after_delete" "DELETE"
echo "Expected after DELETE: $expected_after_delete minutes"
echo "Actual after DELETE:   $ACTUAL_WORKLOAD minutes"
echo "PASS: API-to-workload-service integration is working."
