package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidEntityExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        InvalidEntityException exception = new InvalidEntityException("Invalid entity state");

        assertEquals("Invalid entity state", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessage() {
        String expectedMessage = "Entity validation failed";

        InvalidEntityException exception = new InvalidEntityException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
