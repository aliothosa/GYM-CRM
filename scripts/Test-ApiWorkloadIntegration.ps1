$ErrorActionPreference = 'Stop'

$ScriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path
$RepositoryRoot = Resolve-Path (Join-Path $ScriptDirectory '..')
$ApiUrl = if ($env:GYMCRM_API_URL) { $env:GYMCRM_API_URL } else { 'http://localhost:8080' }
$WorkloadUrl = if ($env:WORKLOAD_SERVICE_URL) { $env:WORKLOAD_SERVICE_URL } else { 'http://localhost:8081' }
$TimeoutSeconds = if ($env:INTEGRATION_TIMEOUT_SECONDS) { [int]$env:INTEGRATION_TIMEOUT_SECONDS } else { 15 }
$ApiUrl = $ApiUrl.TrimEnd('/')
$WorkloadUrl = $WorkloadUrl.TrimEnd('/')
if ($TimeoutSeconds -lt 1) { throw 'INTEGRATION_TIMEOUT_SECONDS must be a positive integer.' }

$TrainerUsername = 'John.Doe'; $TraineeUsername = 'Emily.Davis'; $Year = 2030; $Month = 7; $Date = '2030-07-21'
$FirstDuration = 60; $SecondDuration = 90; $Token = $null; $FirstId = $null; $SecondId = $null; $Baseline = 0

function Get-SafeError([object]$ErrorRecord) {
    $response = $ErrorRecord.Exception.Response
    if ($response) { return "HTTP $([int]$response.StatusCode)" }
    return $ErrorRecord.Exception.Message.Substring(0, [Math]::Min(300, $ErrorRecord.Exception.Message.Length))
}
function Invoke-JsonRequest([string]$Method, [string]$Url, [object]$Body = $null) {
    $headers = @{ Accept = 'application/json' }
    if ($script:Token) { $headers.Authorization = "Bearer $script:Token" }
    try {
        $params = @{ Method = $Method; Uri = $Url; Headers = $headers; TimeoutSec = 10; ErrorAction = 'Stop' }
        if ($null -ne $Body) { $params.ContentType = 'application/json'; $params.Body = ($Body | ConvertTo-Json -Compress) }
        $script:LastResponse = Invoke-WebRequest @params
        $script:LastStatus = [int]$LastResponse.StatusCode
        $script:LastBody = $LastResponse.Content
    } catch {
        $status = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { 0 }
        $script:LastStatus = $status; $script:LastBody = Get-SafeError $_
        if ($status -eq 0) { throw "Request failed: $Method $Url. Ensure the application stack is running. $script:LastBody" }
    }
}
function Assert-Status([int]$Expected, [string]$Action) {
    if ($script:LastStatus -ne $Expected) { throw "$Action failed: HTTP $script:LastStatus (expected $Expected). Response: $($script:LastBody.Substring(0, [Math]::Min(300, $script:LastBody.Length)))" }
}
function Test-Ready([string]$Name, [string]$Url, [bool]$AllowUnauthorized = $false) {
    $script:Token = $null; Invoke-JsonRequest 'GET' "$Url/actuator/health"
    if ($script:LastStatus -ne 200 -and -not ($AllowUnauthorized -and $script:LastStatus -eq 401)) { throw "$Name is not reachable at $Url. Start the application stack before running this test." }
}
function Read-Workload {
    Invoke-JsonRequest 'GET' "$WorkloadUrl/workloads/$TrainerUsername`?year=$Year&month=$Month"
    if ($script:LastStatus -eq 404) { $script:ActualWorkload = 0; return }
    Assert-Status 200 'Reading trainer workload'
    $workload = $script:LastBody | ConvertFrom-Json
    $yearSummary = @($workload.years | Where-Object { $_.year -eq $Year })[0]
    $monthSummary = @($yearSummary.months | Where-Object { $_.month -eq $Month })[0]
    if ($workload.trainerUsername -ne $TrainerUsername -or $null -eq $monthSummary) { throw 'Workload response did not contain the requested trainer/year/month.' }
    $script:ActualWorkload = [int64]$monthSummary.trainingSummaryDurationMinutes
}
function Wait-ForWorkload([int64]$Expected, [string]$Label) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do { Read-Workload; if ($ActualWorkload -eq $Expected) { return }; Start-Sleep -Seconds 1 } while ((Get-Date) -le $deadline)
    throw "$Label workload assertion failed after $TimeoutSeconds seconds: expected $Expected minutes, last observed $ActualWorkload minutes."
}
function Find-TrainingId([string]$Name, [int]$Duration) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        Invoke-JsonRequest 'GET' "$ApiUrl/trainers/$TrainerUsername/trainings-simple"; Assert-Status 200 'Listing trainer trainings'
        $match = @($script:LastBody | ConvertFrom-Json | Where-Object { $_.embeddedResponse.name -eq $Name -and $_.embeddedResponse.date -eq $Date -and $_.embeddedResponse.traineeName -eq $TraineeUsername -and $_.embeddedResponse.duration -eq $Duration })[0]
        if ($match) { return [int64]$match.id }; Start-Sleep -Seconds 1
    } while ((Get-Date) -le $deadline)
    throw "Could not locate newly created training '$Name' within $TimeoutSeconds seconds."
}
function Remove-TestTraining([Nullable[Int64]]$Id) {
    if ($null -eq $Id) { return $true }
    Invoke-JsonRequest 'DELETE' "$ApiUrl/trainers/$TrainerUsername/trainings/$Id"
    if ($LastStatus -notin 204,404) { Write-Error "Cleanup failed for training $Id: HTTP $LastStatus. Response: $LastBody"; return $false }
    return $true
}

$testFailure = $null
try {
    Write-Host '[1/7] Checking service availability...'; Test-Ready 'gym-crm-api' $ApiUrl $true; Test-Ready 'trainer-workload-service' $WorkloadUrl
    Write-Host '[2/7] Authenticating as Emily.Davis...'; Invoke-JsonRequest 'POST' "$ApiUrl/auth/login" @{ username = 'Emily.Davis'; password = 'pass123' }; Assert-Status 200 'Authentication'
    $Token = ($LastBody | ConvertFrom-Json).accessToken; if ([string]::IsNullOrWhiteSpace($Token)) { throw 'Authentication response did not contain a non-empty accessToken.' }; Write-Host 'Authentication successful.'
    Write-Host '[3/7] Reading baseline workload...'; Read-Workload; $Baseline = $ActualWorkload; Write-Host "Baseline workload: $Baseline minutes"
    $suffix = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds(); $firstName = "API workload integration $suffix-A"; $secondName = "API workload integration $suffix-B"
    Write-Host '[4/7] Creating two trainings...'; Invoke-JsonRequest 'POST' "$ApiUrl/trainings" @{ traineeUsername=$TraineeUsername; trainerUsername=$TrainerUsername; trainingName=$firstName; date=$Date; durationInMinutes=$FirstDuration }; Assert-Status 200 'Creating first training'; $FirstId = Find-TrainingId $firstName $FirstDuration
    Invoke-JsonRequest 'POST' "$ApiUrl/trainings" @{ traineeUsername=$TraineeUsername; trainerUsername=$TrainerUsername; trainingName=$secondName; date=$Date; durationInMinutes=$SecondDuration }; Assert-Status 200 'Creating second training'; $SecondId = Find-TrainingId $secondName $SecondDuration
    $expectedAdd = $Baseline + $FirstDuration + $SecondDuration; Write-Host '[5/7] Verifying workload after ADD events...'; Wait-ForWorkload $expectedAdd 'ADD'; Write-Host "Expected after ADD: $expectedAdd minutes"; Write-Host "Actual after ADD:   $ActualWorkload minutes"
    Write-Host "[6/7] Deleting training $FirstId..."; Invoke-JsonRequest 'DELETE' "$ApiUrl/trainers/$TrainerUsername/trainings/$FirstId"; Assert-Status 204 'Deleting first training'; $FirstId = $null
    $expectedDelete = $Baseline + $SecondDuration; Write-Host '[7/7] Verifying workload after DELETE event...'; Wait-ForWorkload $expectedDelete 'DELETE'; Write-Host "Expected after DELETE: $expectedDelete minutes"; Write-Host "Actual after DELETE:   $ActualWorkload minutes"; Write-Host 'PASS: API-to-workload-service integration is working.'
} catch { $testFailure = $_; Write-Error $_ } finally {
    $cleanupOk = (Remove-TestTraining $FirstId) -and (Remove-TestTraining $SecondId)
    if ($Token -and $cleanupOk) { try { Wait-ForWorkload $Baseline 'Cleanup'; Write-Host "Cleanup restored workload baseline: $Baseline minutes" } catch { Write-Error $_; $cleanupOk = $false } }
    if ($testFailure) { exit 1 }; if (-not $cleanupOk) { exit 1 }
}
