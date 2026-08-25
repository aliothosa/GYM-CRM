package com.elioth.epam.workload.component;

import com.elioth.epam.workload.persistence.TrainerWorkloadDocument;
import com.elioth.epam.workload.repository.TrainerWorkloadRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class TrainerWorkloadComponentSteps {

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private TrainerWorkloadRepository repository;

    private final AtomicReference<TrainerWorkloadDocument> storedWorkload = new AtomicReference<>();
    private MockMvc mockMvc;
    private MvcResult response;

    @Before
    public void resetComponentState() {
        mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        storedWorkload.set(null);
        reset(repository);
        org.mockito.Mockito.when(repository.findByTrainerUsername(any())).thenAnswer(invocation -> {
            TrainerWorkloadDocument workload = storedWorkload.get();
            return workload != null && workload.getTrainerUsername().equals(invocation.getArgument(0))
                    ? Optional.of(workload)
                    : Optional.empty();
        });
        doAnswer(invocation -> {
            storedWorkload.set(invocation.getArgument(0));
            return invocation.getArgument(0);
        }).when(repository).save(any(TrainerWorkloadDocument.class));
    }

    @Given("no workload exists for trainer {string}")
    public void noWorkloadExistsForTrainer(String username) {
        Assertions.assertTrue(storedWorkload.get() == null);
    }

    @When("the authenticated client adds {int} workload minutes for trainer {string} on {string}")
    public void authenticatedClientAddsWorkload(int duration, String username, String date) throws Exception {
        response = mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"trainerUsername":"%s","trainerFirstName":"John","trainerLastName":"Doe","active":true,"trainingDate":"%s","trainingDurationMinutes":%d,"actionType":"ADD"}
                                """.formatted(username, date, duration)))
                .andReturn();
    }

    @When("the authenticated client deletes {int} workload minutes for trainer {string} on {string}")
    public void authenticatedClientDeletesWorkload(int duration, String username, String date) throws Exception {
        response = mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"trainerUsername":"%s","trainerFirstName":"John","trainerLastName":"Doe","active":true,"trainingDate":"%s","trainingDurationMinutes":%d,"actionType":"DELETE"}
                                """.formatted(username, date, duration)))
                .andReturn();
    }

    @When("the authenticated client submits a workload with zero minutes for trainer {string}")
    public void authenticatedClientSubmitsZeroDuration(String username) throws Exception {
        authenticatedClientAddsWorkload(0, username, "2026-08-05");
    }

    @Then("the workload update is accepted")
    public void workloadUpdateIsAccepted() {
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Then("the stored monthly workload for trainer {string} in {int}-{int} is {int} minutes")
    public void storedMonthlyWorkloadIs(String username, int year, int month, int duration) {
        TrainerWorkloadDocument document = storedWorkload.get();
        Assertions.assertNotNull(document);
        Assertions.assertEquals(username, document.getTrainerUsername());
        Assertions.assertEquals((long) duration, document.getYears().stream()
                .filter(candidate -> candidate.getYear() == year)
                .findFirst().orElseThrow().getMonths().stream()
                .filter(candidate -> candidate.getMonth() == month)
                .findFirst().orElseThrow().getTrainingsSummaryDuration());
        verify(repository).save(document);
    }

    @Then("the authenticated client can retrieve {int} workload minutes for trainer {string} in {int}-{int}")
    public void authenticatedClientCanRetrieveMonthlyWorkload(int duration, String username, int year, int month)
            throws Exception {
        mockMvc.perform(get("/workloads/{username}", username)
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value(username))
                .andExpect(jsonPath("$.years[0].year").value(year))
                .andExpect(jsonPath("$.years[0].months[0].month").value(month))
                .andExpect(jsonPath("$.years[0].months[0].trainingSummaryDurationMinutes").value(duration));
    }

    @Then("the workload update is rejected")
    public void workloadUpdateIsRejected() {
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Then("no workload is stored")
    public void noWorkloadIsStored() {
        Assertions.assertNull(storedWorkload.get());
    }
}
