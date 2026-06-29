package com.elioth.epam.gymcrm.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Optional/low-priority DTO record test; enable when practicing record construction and accessors.")
class DtoRecordsTest {

    @Test
    void createTraineeRequest_shouldExposeComponents() {
        // TODO: build CreateTraineeRequest with sample firstName, lastName, birthDate, Address
        // TODO: assert accessor methods return the same values
    }

    @Test
    void createTrainerRequest_shouldExposeComponents() {
        // TODO: build CreateTrainerRequest with firstName, lastName, trainingTypeId
        // TODO: assert accessor methods return the same values
    }

    @Test
    void createTrainingRequest_shouldExposeComponents() {
        // TODO: build CreateTrainingRequest with traineeId, trainerId, trainingTypeId, name, date, duration
        // TODO: assert accessor methods return the same values
    }

    @Test
    void changePasswordRequest_shouldExposeComponents() {
        // TODO: build ChangePasswordRequest("old", "new")
        // TODO: assert oldPassword() and newPassword() match
    }

    @Test
    void updateTraineeRequest_shouldExposeComponents() {
        // TODO: build UpdateTraineeRequest with name, birthDate, Address fields
        // TODO: assert accessor methods return the same values
    }

    @Test
    void updateTrainerRequest_shouldExposeComponents() {
        // TODO: build UpdateTrainerRequest with firstName, lastName, trainingTypeId
        // TODO: assert accessor methods return the same values
    }

    @Test
    void updateTrainingRequest_shouldExposeComponents() {
        // TODO: build UpdateTrainingRequest with name, date, duration
        // TODO: assert accessor methods return the same values
    }

    @Test
    void traineeResponse_shouldExposeComponents() {
        // TODO: build TraineeResponse with ids, names, username, active, birthDate, address
        // TODO: assert accessor methods return the same values
    }

    @Test
    void trainerResponse_shouldExposeComponents() {
        // TODO: build TrainerResponse with ids, names, username, active, trainingTypeId, trainingTypeName
        // TODO: assert accessor methods return the same values
    }

    @Test
    void trainingResponse_shouldExposeComponents() {
        // TODO: build TrainingResponse with nested trainee/trainer/type fields and training metadata
        // TODO: assert accessor methods return the same values
    }

    @Test
    void createdTraineeResponse_shouldExposeComponents() {
        // TODO: build CreatedTraineeResponse(traineeId, username, password)
        // TODO: assert accessor methods return the same values
    }

    @Test
    void createdTrainerResponse_shouldExposeComponents() {
        // TODO: build CreatedTrainerResponse(trainerId, username, password)
        // TODO: assert accessor methods return the same values
    }
}
