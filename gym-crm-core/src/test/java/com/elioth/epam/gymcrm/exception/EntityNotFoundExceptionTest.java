package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        EntityNotFoundException exception = new EntityNotFoundException("Trainee not found");

        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithEntityNameAndId() {
        EntityNotFoundException exception = new EntityNotFoundException("Trainee", 42L);

        assertEquals("Trainee not found with id: 42", exception.getMessage());
    }

    @Test
    void shouldPreserveErrorMessage() {
        String expectedMessage = "Custom not found message";

        EntityNotFoundException exception = new EntityNotFoundException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
