package com.elioth.epam.gymcrm.dto.mapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class TrainerMapperTest {

    @Test
    @Disabled("Practice skeleton")
    void shouldMapUserAndSpecializationToEntity() {
        // Arrange
        // TODO: create User and TrainingType

        // Act
        // TODO: Trainer trainer = TrainerMapper.toEntity(user, specialization);

        // Assert
        // TODO: assert user and specialization are set on trainer
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTrainerSpecialization() {
        // Arrange
        // TODO: create Trainer and new TrainingType

        // Act
        // TODO: TrainerMapper.updateEntity(trainer, newSpecialization);

        // Assert
        // TODO: assert trainer specialization is updated
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapTrainerToResponse() {
        // Arrange
        // TODO: create Trainer with associated User and TrainingType

        // Act
        // TODO: TrainerResponse response = TrainerMapper.toResponse(trainer);

        // Assert
        // TODO: assert all response fields match trainer, user, and specialization data
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapTrainerToCreatedResponse() {
        // Arrange
        // TODO: create Trainer with associated User

        // Act
        // TODO: CreatedTrainerResponse response = TrainerMapper.toCreatedResponse(trainer);

        // Assert
        // TODO: assert trainerId, username, and password are mapped correctly
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldHandleNullSpecializationInResponse() {
        // Arrange
        // TODO: create Trainer with user but specialization = null

        // Act
        // TODO: TrainerResponse response = TrainerMapper.toResponse(trainer);

        // Assert
        // TODO: assert specializationId and specializationName are null
    }
}
