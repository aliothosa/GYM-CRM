package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnauthorizedOperationExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        UnauthorizedOperationException exception = new UnauthorizedOperationException("Unauthorized operation");

        assertEquals("Unauthorized operation", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessage() {
        String expectedMessage = "User lacks required permissions";

        UnauthorizedOperationException exception = new UnauthorizedOperationException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
