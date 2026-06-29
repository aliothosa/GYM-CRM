package com.elioth.epam.gymcrm.dto.mapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class TrainingMapperTest {

    @Test
    @Disabled("Practice skeleton")
    void shouldMapCreateTrainingRequestToEntity() {
        // Arrange
        // TODO: create CreateTrainingRequest, Trainee, Trainer, and TrainingType

        // Act
        // TODO: Training training = TrainingMapper.toEntity(request, trainee, trainer, trainingType);

        // Assert
        // TODO: assert trainee, trainer, type, name, date, and duration are mapped correctly
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTrainingEntityFromRequest() {
        // Arrange
        // TODO: create Training and UpdateTrainingRequest

        // Act
        // TODO: TrainingMapper.updateEntity(training, request);

        // Assert
        // TODO: assert name, date, and duration are updated
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapTrainingToResponse() {
        // Arrange
        // TODO: create Training with trainee, trainer, type, and associated users

        // Act
        // TODO: TrainingResponse response = TrainingMapper.toResponse(training);

        // Assert
        // TODO: assert all response fields match training and participant data
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldHandleNullRelationsWhenMappingToResponse() {
        // Arrange
        // TODO: create Training with null trainee, trainer, or trainingType

        // Act & Assert
        // TODO: assertThrows(IllegalStateException.class, () -> TrainingMapper.toResponse(training))

        // Arrange
        // TODO: create Training where trainee or trainer has no associated user

        // Act & Assert
        // TODO: assertThrows(IllegalStateException.class, () -> TrainingMapper.toResponse(training))
    }
}
