package com.elioth.epam.gymcrm.dto;

import com.elioth.epam.gymcrm.domain.Address;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoRecordsTest {

    @Test
    void createTraineeRequest_shouldExposeComponents() {
        Address address = new Address("Main St", "Boston", "MA", "02101", 42);
        LocalDate birthDate = LocalDate.of(1995, 3, 10);
        CreateTraineeRequest request = new CreateTraineeRequest("Jane", "Doe", birthDate, address);

        assertEquals("Jane", request.firstName());
        assertEquals("Doe", request.lastName());
        assertEquals(birthDate, request.birthDate());
        assertEquals(address, request.address());
    }

    @Test
    void createTrainerRequest_shouldExposeComponents() {
        CreateTrainerRequest request = new CreateTrainerRequest("Bob", "Smith", 3L);

        assertEquals("Bob", request.firstName());
        assertEquals("Smith", request.lastName());
        assertEquals(3L, request.trainingTypeId());
    }

    @Test
    void createTrainingRequest_shouldExposeComponents() {
        LocalDate trainingDate = LocalDate.of(2024, 7, 15);
        CreateTrainingRequest request = new CreateTrainingRequest(
                1L, 2L, 3L, "Morning Yoga", trainingDate, 90L
        );

        assertEquals(1L, request.traineeId());
        assertEquals(2L, request.trainerId());
        assertEquals(3L, request.trainingTypeId());
        assertEquals("Morning Yoga", request.trainingName());
        assertEquals(trainingDate, request.trainingDate());
        assertEquals(90L, request.duration());
    }

    @Test
    void changePasswordRequest_shouldExposeComponents() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");

        assertEquals("oldPass", request.oldPassword());
        assertEquals("newPass", request.newPassword());
    }

    @Test
    void updateTraineeRequest_shouldExposeComponents() {
        Address address = new Address("Elm St", "Austin", "TX", "73301", 7);
        LocalDate birthDate = LocalDate.of(1992, 8, 20);
        UpdateTraineeRequest request = new UpdateTraineeRequest("Updated", "Name", birthDate, address);

        assertEquals("Updated", request.firstName());
        assertEquals("Name", request.lastName());
        assertEquals(birthDate, request.birthDate());
        assertEquals(address, request.address());
    }

    @Test
    void updateTrainerRequest_shouldExposeComponents() {
        UpdateTrainerRequest request = new UpdateTrainerRequest("New", "Trainer", 5L);

        assertEquals("New", request.firstName());
        assertEquals("Trainer", request.lastName());
        assertEquals(5L, request.trainingTypeId());
    }

    @Test
    void updateTrainingRequest_shouldExposeComponents() {
        LocalDate date = LocalDate.of(2024, 9, 1);
        UpdateTrainingRequest request = new UpdateTrainingRequest("Evening Session", date, 45L);

        assertEquals("Evening Session", request.name());
        assertEquals(date, request.date());
        assertEquals(45L, request.duration());
    }

    @Test
    void traineeResponse_shouldExposeComponents() {
        Address address = new Address("Pine Rd", "Denver", "CO", "80202", 15);
        LocalDate birthDate = LocalDate.of(1988, 1, 5);
        TraineeResponse response = new TraineeResponse(
                10L, 20L, "Alice", "Wonder", "alice.wonder", true, birthDate, address
        );

        assertEquals(10L, response.traineeId());
        assertEquals(20L, response.userId());
        assertEquals("Alice", response.firstName());
        assertEquals("Wonder", response.lastName());
        assertEquals("alice.wonder", response.username());
        assertTrue(response.active());
        assertEquals(birthDate, response.birthDate());
        assertEquals(address, response.address());
    }

    @Test
    void trainerResponse_shouldExposeComponents() {
        TrainerResponse response = new TrainerResponse(
                1L, 2L, "Carl", "Lee", "carl.lee", true, 3L, "CrossFit"
        );

        assertEquals(1L, response.trainerId());
        assertEquals(2L, response.userId());
        assertEquals("Carl", response.firstName());
        assertEquals("Lee", response.lastName());
        assertEquals("carl.lee", response.username());
        assertTrue(response.active());
        assertEquals(3L, response.trainingTypeId());
        assertEquals("CrossFit", response.trainingTypeName());
    }

    @Test
    void trainingResponse_shouldExposeComponents() {
        LocalDate trainingDate = LocalDate.of(2024, 10, 5);
        TrainingResponse response = new TrainingResponse(
                100L,
                1L, "trainee.user", "Train", "One",
                2L, "trainer.user", "Coach", "Two",
                3L, "Yoga",
                "Sunrise Flow", trainingDate, 60L
        );

        assertEquals(100L, response.trainingId());
        assertEquals(1L, response.traineeId());
        assertEquals("trainee.user", response.traineeUsername());
        assertEquals("Train", response.traineeFirstName());
        assertEquals("One", response.traineeLastName());
        assertEquals(2L, response.trainerId());
        assertEquals("trainer.user", response.trainerUsername());
        assertEquals("Coach", response.trainerFirstName());
        assertEquals("Two", response.trainerLastName());
        assertEquals(3L, response.trainingTypeId());
        assertEquals("Yoga", response.trainingTypeName());
        assertEquals("Sunrise Flow", response.trainingName());
        assertEquals(trainingDate, response.trainingDate());
        assertEquals(60L, response.duration());
    }

    @Test
    void createdTraineeResponse_shouldExposeComponents() {
        CreatedTraineeResponse response = new CreatedTraineeResponse(5L, "new.trainee", "generatedPass");

        assertEquals(5L, response.traineeId());
        assertEquals("new.trainee", response.username());
        assertEquals("generatedPass", response.password());
    }

    @Test
    void createdTrainerResponse_shouldExposeComponents() {
        CreatedTrainerResponse response = new CreatedTrainerResponse(7L, "new.trainer", "generatedPass");

        assertEquals(7L, response.trainerId());
        assertEquals("new.trainer", response.username());
        assertEquals("generatedPass", response.password());
    }
}
