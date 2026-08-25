package com.elioth.epam.gymcrm.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class MicroserviceIntegrationSteps {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl = withoutTrailingSlash(environment("GYMCRM_API_URL", "http://localhost:8080"));
    private final String workloadUrl = withoutTrailingSlash(environment("WORKLOAD_SERVICE_URL", "http://localhost:8081"));
    private final Duration timeout = Duration.ofSeconds(Long.parseLong(environment("INTEGRATION_TIMEOUT_SECONDS", "15")));
    private final String trainerUsername = environment("INTEGRATION_TRAINER_USERNAME", "John.Doe");
    private final String traineeUsername = environment("INTEGRATION_TRAINEE_USERNAME", "Emily.Davis");
    private final String traineePassword = environment("INTEGRATION_TRAINEE_PASSWORD", "pass123");
    private final int year = Integer.parseInt(environment("INTEGRATION_WORKLOAD_YEAR", "2031"));
    private final int month = Integer.parseInt(environment("INTEGRATION_WORKLOAD_MONTH", "7"));
    private final String trainingDate = environment("INTEGRATION_TRAINING_DATE", "2031-07-21");
    private final int trainingDuration = Integer.parseInt(environment("INTEGRATION_TRAINING_DURATION_MINUTES", "60"));

    private String token;
    private String trainingName;
    private Long createdTrainingId;
    private Long deletedTrainingId;
    private long baselineWorkload;
    private HttpResponse<String> response;

    @Given("the Gym CRM API and trainer workload service are available")
    public void servicesAreAvailable() throws Exception {
        awaitService(apiUrl + "/actuator/health", 200, 401);
        awaitService(workloadUrl + "/actuator/health", 200);
    }

    @Given("an authenticated API client has recorded the baseline workload for trainer John Doe")
    public void authenticatedClientHasBaselineWorkload() throws Exception {
        response = post(apiUrl + "/auth/login", """
                {"username":"%s","password":"%s"}
                """.formatted(traineeUsername, traineePassword), null);
        assertStatus(response, 200);
        token = json(response.body()).path("accessToken").asText();
        Assertions.assertFalse(token.isBlank(), "The API login response must contain an access token");
        baselineWorkload = monthlyWorkload();
    }

    @When("the client creates a training for John Doe through the Gym CRM API")
    public void clientCreatesTraining() throws Exception {
        trainingName = "Cucumber microservice integration " + UUID.randomUUID();
        response = post(apiUrl + "/trainings", """
                {"traineeUsername":"%s","trainerUsername":"%s","trainingName":"%s","date":"%s","durationInMinutes":%d}
                """.formatted(traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration), token);
        assertStatus(response, 200);
        createdTrainingId = awaitTrainingId();
    }

    @Then("the trainer workload service eventually reports the added workload")
    public void workloadServiceEventuallyReportsAddedWorkload() throws Exception {
        awaitWorkload(baselineWorkload + trainingDuration);
    }

    @When("the client deletes the created training through the Gym CRM API")
    public void clientDeletesCreatedTraining() throws Exception {
        Assertions.assertNotNull(createdTrainingId, "A training must be created before it can be deleted");
        deletedTrainingId = createdTrainingId;
        response = delete(apiUrl + "/trainers/" + trainerUsername + "/trainings/" + createdTrainingId, token);
        assertStatus(response, 204);
        createdTrainingId = null;
    }

    @Then("the trainer workload service eventually returns to the baseline workload")
    public void workloadServiceEventuallyReturnsToBaseline() throws Exception {
        awaitWorkload(baselineWorkload);
    }

    @When("the client deletes the same training again through the Gym CRM API")
    public void clientDeletesSameTrainingAgain() throws Exception {
        Assertions.assertNotNull(deletedTrainingId, "A training must be deleted before it can be deleted again");
        response = delete(apiUrl + "/trainers/" + trainerUsername + "/trainings/" + deletedTrainingId, token);
    }

    @Then("the Gym CRM API rejects the repeated deletion")
    public void apiRejectsRepeatedDeletion() {
        assertStatus(response, 404);
    }

    @Then("the trainer workload service still reports the baseline workload")
    public void workloadServiceStillReportsBaseline() throws Exception {
        awaitWorkload(baselineWorkload);
    }

    @After
    public void removeCreatedTraining() throws Exception {
        if (createdTrainingId == null || token == null) {
            return;
        }
        HttpResponse<String> cleanup = delete(
                apiUrl + "/trainers/" + trainerUsername + "/trainings/" + createdTrainingId,
                token
        );
        if (cleanup.statusCode() == 204) {
            awaitWorkload(baselineWorkload);
        }
    }

    private long monthlyWorkload() throws Exception {
        HttpResponse<String> workload = get(workloadUrl + "/workloads/" + trainerUsername
                + "?year=" + year + "&month=" + month, token);
        if (workload.statusCode() == 404) {
            return 0;
        }
        assertStatus(workload, 200);
        return json(workload.body()).path("years").get(0).path("months").get(0)
                .path("trainingSummaryDurationMinutes").asLong();
    }

    private void awaitWorkload(long expected) throws Exception {
        Instant deadline = Instant.now().plus(timeout);
        long observed = Long.MIN_VALUE;
        while (!Instant.now().isAfter(deadline)) {
            observed = monthlyWorkload();
            if (observed == expected) {
                return;
            }
            Thread.sleep(250);
        }
        Assertions.fail("Expected workload %d minutes but observed %d minutes after %d seconds"
                .formatted(expected, observed, timeout.toSeconds()));
    }

    private Long awaitTrainingId() throws Exception {
        Instant deadline = Instant.now().plus(timeout);
        while (!Instant.now().isAfter(deadline)) {
            HttpResponse<String> trainings = get(apiUrl + "/trainers/" + trainerUsername + "/trainings-simple", token);
            assertStatus(trainings, 200);
            for (JsonNode training : json(trainings.body())) {
                JsonNode details = training.path("embeddedResponse");
                if (trainingName.equals(details.path("name").asText())
                        && trainingDate.equals(details.path("date").asText())
                        && details.path("duration").asInt() == trainingDuration) {
                    return training.path("id").asLong();
                }
            }
            Thread.sleep(250);
        }
        throw new AssertionError("The created training was not returned by the Gym CRM API");
    }

    private HttpResponse<String> get(String url, String bearerToken) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url)).GET()
                .header("Accept", "application/json");
        authorize(request, bearerToken);
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String url, String body, String bearerToken) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        authorize(request, bearerToken);
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String url, String bearerToken) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url)).DELETE()
                .header("Accept", "application/json");
        authorize(request, bearerToken);
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void authorize(HttpRequest.Builder request, String bearerToken) {
        if (bearerToken != null && !bearerToken.isBlank()) {
            request.header("Authorization", "Bearer " + bearerToken);
        }
    }

    private void awaitService(String healthUrl, int... expectedStatuses) throws Exception {
        Instant deadline = Instant.now().plus(timeout);
        Throwable lastFailure = null;
        while (!Instant.now().isAfter(deadline)) {
            try {
                HttpResponse<String> health = get(healthUrl, null);
                for (int expectedStatus : expectedStatuses) {
                    if (health.statusCode() == expectedStatus) {
                        return;
                    }
                }
                lastFailure = new AssertionError("Unexpected health status " + health.statusCode());
            } catch (IOException exception) {
                lastFailure = exception;
            }
            Thread.sleep(250);
        }
        AssertionError unavailable = new AssertionError("Service at " + healthUrl + " did not become available within "
                + timeout.toSeconds() + " seconds");
        if (lastFailure != null) {
            unavailable.initCause(lastFailure);
        }
        throw unavailable;
    }

    private JsonNode json(String body) throws IOException {
        return objectMapper.readTree(body);
    }

    private void assertStatus(HttpResponse<String> actual, int... expected) {
        for (int status : expected) {
            if (actual.statusCode() == status) {
                return;
            }
        }
        Assertions.fail("Expected HTTP status %s but received %d: %s"
                .formatted(java.util.Arrays.toString(expected), actual.statusCode(), actual.body()));
    }

    private static String environment(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String withoutTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
