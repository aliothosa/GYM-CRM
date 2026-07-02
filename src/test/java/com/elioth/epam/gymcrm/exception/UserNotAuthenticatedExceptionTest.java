package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNotAuthenticatedExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        UserNotAuthenticatedException exception = new UserNotAuthenticatedException("User not authenticated");

        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessage() {
        String expectedMessage = "No active session found";

        UserNotAuthenticatedException exception = new UserNotAuthenticatedException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
