# Container and integration scripts

## Inspecting container logs

The Compose environment must already be running. These helpers only print safe
log-follow commands; they never attach to, start, stop, or restart containers.
They prefer `docker compose`, then `podman compose`, then `podman-compose`.

```bash
./scripts/show-container-logs.sh
./scripts/show-container-logs.sh trainer-workload-service
```

```powershell
.\scripts\show-container-logs.ps1
.\scripts\show-container-logs.ps1 trainer-workload-service
```

For example, with Docker Compose, the equivalent direct command is:

```bash
docker compose logs --tail 100 -f trainer-workload-service
```

`logs -f` is preferred over `attach` because it follows output without attaching
to the container process. Press `Ctrl+C` to stop following logs; the container
continues running.

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
