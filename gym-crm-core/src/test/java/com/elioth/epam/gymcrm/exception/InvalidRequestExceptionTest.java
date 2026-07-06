package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidRequestExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        InvalidRequestException exception = new InvalidRequestException("Invalid request");

        assertEquals("Invalid request", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessage() {
        String expectedMessage = "Request payload is malformed";

        InvalidRequestException exception = new InvalidRequestException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
