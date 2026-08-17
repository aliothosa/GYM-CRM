package com.elioth.epam.gymcrm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/trainings");

    @Test
    void hidesUnexpectedExceptionDetails() {
        var response = handler.unexpected(new NullPointerException("sensitive implementation detail"), request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected internal server error", response.getBody().message());
        assertFalse(response.getBody().message().contains("sensitive"));
    }
}
