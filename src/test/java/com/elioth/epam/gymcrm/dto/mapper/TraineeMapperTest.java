package com.elioth.epam.gymcrm.dto.mapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class TraineeMapperTest {

    @Test
    @Disabled("Practice skeleton")
    void shouldMapCreateTraineeRequestToEntity() {
        // Arrange
        // TODO: create CreateTraineeRequest and User

        // Act
        // TODO: Trainee trainee = TraineeMapper.toEntity(request, user);

        // Assert
        // TODO: assert user, birthDate, and address are mapped correctly
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTraineeEntityFromRequest() {
        // Arrange
        // TODO: create Trainee and UpdateTraineeRequest

        // Act
        // TODO: TraineeMapper.updateEntity(trainee, request);

        // Assert
        // TODO: assert birthDate and address are updated
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapTraineeToResponse() {
        // Arrange
        // TODO: create Trainee with associated User

        // Act
        // TODO: TraineeResponse response = TraineeMapper.toResponse(trainee);

        // Assert
        // TODO: assert all response fields match trainee and user data
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapTraineeToCreatedResponse() {
        // Arrange
        // TODO: create Trainee with associated User

        // Act
        // TODO: CreatedTraineeResponse response = TraineeMapper.toCreatedResponse(trainee);

        // Assert
        // TODO: assert traineeId, username, and password are mapped correctly
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldReturnNullWhenTraineeIsNull() {
        // Act
        // TODO: call toResponse(null) and toCreatedResponse(null)

        // Assert
        // TODO: assert both methods return null
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowWhenTraineeHasNoAssociatedUser() {
        // Arrange
        // TODO: create Trainee with user = null

        // Act & Assert
        // TODO: assertThrows(IllegalStateException.class, () -> TraineeMapper.toResponse(trainee))
        // TODO: assertThrows(IllegalStateException.class, () -> TraineeMapper.toCreatedResponse(trainee))
    }
}
