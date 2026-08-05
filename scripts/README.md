# Container and integration scripts

## Recreate the complete stack on a new machine

This workflow starts PostgreSQL, Eureka, the API, and the trainer workload
service from a fresh clone. It does not require a locally installed JDK or
Maven because the container builds compile the applications.

### Prerequisites

- Git
- Podman and the `podman compose` provider
- `curl` and `jq` to run the integration check
- Ports `5432`, `8080`, `8081`, and `8761` available on the host

Clone the repository and enter it:

```bash
git clone <repository-url> GYM-CRM
cd GYM-CRM
```

Set the two required secrets in your current shell. Use private, strong values;
do not commit them or paste them into logs. The JWT secret must be at least 32
characters.

```bash
read -rsp 'PostgreSQL password: ' POSTGRES_PASSWORD; echo
export POSTGRES_PASSWORD

read -rsp 'JWT secret (32+ characters): ' GYMCRM_JWT_SECRET; echo
export GYMCRM_JWT_SECRET
```

Build and start the complete stack:

```bash
podman compose build
podman compose up -d
podman compose ps
```

Follow startup logs directly from the containers:

```bash
podman compose logs -f discovery-server
podman compose logs -f trainer-workload-service
podman compose logs -f gym-crm-api
```

Once the services have started, the following endpoints should respond:

```bash
curl http://localhost:8761/actuator/health
curl http://localhost:8081/actuator/health
curl -i http://localhost:8080/actuator/health  # API security returns 401 when healthy
```

Then run the end-to-end check described below:

```bash
./scripts/test-api-workload-integration.sh
```

### Rebuild one changed service

For an API-only code or configuration change, rebuild and recreate only that
container; its dependencies keep running:

```bash
podman compose build gym-crm-api
podman compose up -d --no-deps --force-recreate gym-crm-api
podman compose logs -f gym-crm-api
```

Use the same pattern for the workload service:

```bash
podman compose build trainer-workload-service
podman compose up -d --no-deps --force-recreate trainer-workload-service
podman compose logs -f trainer-workload-service
```

### Stop or reset the stack

Stop containers while retaining PostgreSQL data:

```bash
podman compose down
```

To remove the named PostgreSQL volume too (this permanently deletes local
database data), run:

```bash
podman compose down -v
```

## API-to-workload integration test

`test-api-workload-integration.sh` and `Test-ApiWorkloadIntegration.ps1` verify
that training creation and deletion through `gym-crm-api` produces the expected
monthly workload changes in `trainer-workload-service`.

Prerequisites: the API, workload service, discovery server, and database must
already be running; Bash requires `curl` and `jq`; PowerShell requires `pwsh`.
Neither script starts applications or containers. Optional settings are
`GYMCRM_API_URL`, `WORKLOAD_SERVICE_URL`, and `INTEGRATION_TIMEOUT_SECONDS`
(default: `http://localhost:8080`, `http://localhost:8081`, and `15`).

```bash
chmod +x scripts/test-api-workload-integration.sh
./scripts/test-api-workload-integration.sh
```

```powershell
pwsh ./scripts/Test-ApiWorkloadIntegration.ps1
```

Both scripts authenticate as the seeded `Emily.Davis` user, create two uniquely
named sessions for seeded trainer `John.Doe`, validate ADD and DELETE workload
totals relative to the observed baseline, and remove test sessions in cleanup.
