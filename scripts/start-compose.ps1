[CmdletBinding()]
param(
    [ValidateRange(1, 600)]
    [int]$StartupTimeoutSeconds = 120
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $repositoryRoot '.env'

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker Desktop with Docker Compose is required.'
}

& docker info *> $null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker is installed but its daemon is not running.'
}

& docker compose version *> $null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker Compose v2 is required.'
}

if (-not (Test-Path -LiteralPath $envFile -PathType Leaf)) {
    throw "Missing environment file: $envFile"
}

Get-Content -LiteralPath $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#')) {
        $parts = $line -split '=', 2
        if ($parts.Count -ne 2 -or [string]::IsNullOrWhiteSpace($parts[0])) {
            throw "Invalid .env entry: $line"
        }
        Set-Item -Path "Env:$($parts[0].Trim())" -Value $parts[1]
    }
}

function Get-HttpStatus([string]$Uri) {
    try {
        return [int](Invoke-WebRequest -UseBasicParsing -Uri $Uri -TimeoutSec 5).StatusCode
    } catch {
        if ($_.Exception.Response) {
            return [int]$_.Exception.Response.StatusCode
        }
        return 0
    }
}

foreach ($requiredVariable in 'POSTGRES_PASSWORD', 'GYMCRM_JWT_SECRET') {
    if ([string]::IsNullOrWhiteSpace((Get-Item -Path "Env:$requiredVariable" -ErrorAction SilentlyContinue).Value)) {
        throw "$requiredVariable must be set in .env"
    }
}

Push-Location $repositoryRoot
try {
    Write-Host 'Building and starting the compose stack with Docker Compose...'
    & docker compose up --build --detach
    if ($LASTEXITCODE -ne 0) {
        throw 'Docker Compose failed to start the stack.'
    }

    $deadline = (Get-Date).AddSeconds($StartupTimeoutSeconds)
    do {
        $apiStatus = Get-HttpStatus 'http://localhost:8080/actuator/health'
        $workloadStatus = Get-HttpStatus 'http://localhost:8081/actuator/health'

        if (($apiStatus -eq 200 -or $apiStatus -eq 401) -and $workloadStatus -eq 200) {
            Write-Host "Stack is ready for testing: gym-crm-api=$apiStatus trainer-workload-service=$workloadStatus"
            exit 0
        }

        Start-Sleep -Seconds 2
    } while ((Get-Date) -le $deadline)

    & docker compose ps
    throw "Timed out after $StartupTimeoutSeconds seconds waiting for the microservices."
} finally {
    Pop-Location
}
