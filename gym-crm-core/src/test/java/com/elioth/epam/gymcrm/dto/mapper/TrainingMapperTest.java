package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainingMapperTest {

    @Test
    void shouldMapCreateTrainingRequestToEntity() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Yoga");
        LocalDate trainingDate = LocalDate.of(2024, 7, 15);
        CreateTrainingRequest request = new CreateTrainingRequest(
                1L, 2L, 3L, "Morning Yoga", trainingDate, 90L
        );

        Training training = TrainingMapper.toEntity(request, trainee, trainer, trainingType);

        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainingType, training.getType());
        assertEquals("Morning Yoga", training.getName());
        assertEquals(trainingDate, training.getDate());
        assertEquals(90L, training.getDurationInMinutes());
    }

    @Test
    void shouldUpdateTrainingEntityFromRequest() {
        Training training = new Training();
        LocalDate newDate = LocalDate.of(2024, 9, 1);
        UpdateTrainingRequest request = new UpdateTrainingRequest("Evening Session", newDate, 45L);

        TrainingMapper.updateEntity(training, request);

        assertEquals("Evening Session", training.getName());
        assertEquals(newDate, training.getDate());
        assertEquals(45L, training.getDurationInMinutes());
    }

    @Test
    void shouldMapTrainingToResponse() {
        User traineeUser = new User();
        traineeUser.setUsername("trainee.user");
        traineeUser.setFirstName("Train");
        traineeUser.setLastName("One");
        Trainee trainee = new Trainee();
        trainee.setTraineeId(1L);
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setUsername("trainer.user");
        trainerUser.setFirstName("Coach");
        trainerUser.setLastName("Two");
        Trainer trainer = new Trainer();
        trainer.setTrainerId(2L);
        trainer.setUser(trainerUser);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(3L);
        trainingType.setName("Yoga");

        LocalDate trainingDate = LocalDate.of(2024, 10, 5);
        Training training = new Training();
        training.setTrainingId(100L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(trainingType);
        training.setName("Sunrise Flow");
        training.setDate(trainingDate);
        training.setDurationInMinutes(60L);

        TrainingResponse response = TrainingMapper.toResponse(training);

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
    void shouldReturnNullWhenTrainingIsNull() {
        assertNull(TrainingMapper.toResponse(null));
    }

    @Test
    void shouldThrowWhenTrainingHasNullRelations() {
        Training training = new Training();
        training.setTrainingId(1L);

        assertThrows(IllegalStateException.class, () -> TrainingMapper.toResponse(training));

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(type);

        assertThrows(IllegalStateException.class, () -> TrainingMapper.toResponse(training));
    }

    @Test
    void shouldThrowWhenParticipantsHaveNoAssociatedUser() {
        Trainee trainee = new Trainee();
        trainee.setTraineeId(1L);
        Trainer trainer = new Trainer();
        trainer.setTrainerId(2L);
        TrainingType type = new TrainingType();
        type.setId(3L);
        type.setName("Cardio");
        Training training = new Training();
        training.setTrainingId(100L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(type);
        training.setName("Session");
        training.setDate(LocalDate.of(2024, 6, 1));
        training.setDurationInMinutes(60L);

        assertThrows(IllegalStateException.class, () -> TrainingMapper.toResponse(training));

        User traineeUser = new User();
        traineeUser.setUsername("trainee.user");
        trainee.setUser(traineeUser);

        assertThrows(IllegalStateException.class, () -> TrainingMapper.toResponse(training));
    }
}
