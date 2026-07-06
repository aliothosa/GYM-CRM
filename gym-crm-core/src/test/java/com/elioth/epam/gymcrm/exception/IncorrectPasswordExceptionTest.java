package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncorrectPasswordExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        IncorrectPasswordException exception = new IncorrectPasswordException("Incorrect password");

        assertEquals("Incorrect password", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessage() {
        String expectedMessage = "Password does not match";

        IncorrectPasswordException exception = new IncorrectPasswordException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
