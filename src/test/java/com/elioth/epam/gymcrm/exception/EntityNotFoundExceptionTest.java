package com.elioth.epam.gymcrm.exception;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class EntityNotFoundExceptionTest {

    @Test
    @Disabled("Practice skeleton")
    void shouldCreateExceptionWithMessage() {
        // Act
        // TODO: EntityNotFoundException exception = new EntityNotFoundException("Trainee not found");

        // Assert
        // TODO: assert exception.getMessage() equals expected message
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldCreateExceptionWithEntityNameAndId() {
        // Act
        // TODO: EntityNotFoundException exception = new EntityNotFoundException("Trainee", 42L);

        // Assert
        // TODO: assert exception.getMessage() equals "Trainee not found with id: 42"
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldPreserveErrorMessage() {
        // Arrange
        // TODO: define expected message string

        // Act
        // TODO: EntityNotFoundException exception = new EntityNotFoundException(expectedMessage);

        // Assert
        // TODO: assert exception.getMessage() is preserved exactly
    }
}
