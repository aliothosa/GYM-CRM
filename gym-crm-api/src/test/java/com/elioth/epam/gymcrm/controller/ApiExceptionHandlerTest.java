package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.client.workload.WorkloadServiceTimeoutException;
import com.elioth.epam.gymcrm.client.workload.WorkloadServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/trainings");

    @Test
    void mapsTimeoutAndUnavailableWorkloadFailuresWithoutLeakingTheirCause() {
        var timeout = handler.workloadTimeout(
                new WorkloadServiceTimeoutException(new IllegalStateException("internal endpoint")), request);
        var unavailable = handler.workloadUnavailable(
                new WorkloadServiceUnavailableException(new IllegalStateException("internal endpoint")), request);

        assertEquals(504, timeout.getStatusCode().value());
        assertEquals("Trainer workload service timed out", timeout.getBody().message());
        assertEquals(503, unavailable.getStatusCode().value());
        assertFalse(unavailable.getBody().message().contains("internal endpoint"));
    }

    @Test
    void hidesUnexpectedExceptionDetails() {
        var response = handler.unexpected(new NullPointerException("sensitive implementation detail"), request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected internal server error", response.getBody().message());
        assertFalse(response.getBody().message().contains("sensitive"));
    }
}
