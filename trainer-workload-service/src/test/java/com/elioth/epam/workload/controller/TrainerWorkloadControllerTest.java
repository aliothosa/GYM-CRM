package com.elioth.epam.workload.controller;

import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.elioth.epam.workload.exception.WorkloadExceptionHandler;
import com.elioth.epam.workload.security.WorkloadSecurityConfig;
import com.elioth.epam.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerWorkloadController.class)
@Import({WorkloadSecurityConfig.class, WorkloadExceptionHandler.class})
@ImportAutoConfiguration({SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class})
@TestPropertySource(properties = "gymcrm.jwt.secret=test-only-jwt-secret-with-at-least-32-characters")
class TrainerWorkloadControllerTest {

    private static final String VALID_REQUEST = """
            {
              "trainerUsername": "john.doe",
              "trainerFirstName": "John",
              "trainerLastName": "Doe",
              "active": true,
              "trainingDate": "2026-08-05",
              "trainingDurationMinutes": 60,
              "actionType": "ADD"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerWorkloadService workloadService;

    @Test
    void rejectsMissingAndInvalidBearerTokens() throws Exception {
        mockMvc.perform(post("/workloads").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/workloads")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptsAValidJwtAndReturnsOk() throws Exception {
        mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isOk());

        verify(workloadService).applyWorkload(any());
    }

    @Test
    void returnsBadRequestForInvalidRequestDataAndUnknownAction() throws Exception {
        mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST.replace("\"ADD\"", "\"UPSERT\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid workload request"));

        mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST.replace("60", "0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid workload request"));
    }

    @Test
    void returnsExpectedBusinessAndNotFoundErrors() throws Exception {
        doThrow(new InvalidWorkloadException("Workload cannot become negative"))
                .when(workloadService).applyWorkload(any());
        mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Workload cannot become negative"));

        doThrow(new TrainerWorkloadNotFoundException("No workload found"))
                .when(workloadService).getMonthlySummary("missing", 2026, 8);
        mockMvc.perform(get("/workloads/missing").param("year", "2026").param("month", "8")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No workload found"));
    }

    @Test
    void hidesUnexpectedExceptionMessagesFromResponses() throws Exception {
        doThrow(new IllegalStateException("database credentials must not leak"))
                .when(workloadService).applyWorkload(any());

        mockMvc.perform(post("/workloads")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected workload service error"));
    }
}
