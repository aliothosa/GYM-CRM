package com.elioth.epam.gymcrm.facade;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymCrmFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymCrmFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymCrmFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void shouldDelegateCreateTraineeProfileToTraineeService() {
        // Given
        Trainee trainee = buildTrainee();
        when(traineeService.createProfile(trainee)).thenReturn(trainee);

        // When
        Trainee result = facade.createTraineeProfile(trainee);

        // Then
        assertEquals(trainee, result);
        verify(traineeService).createProfile(trainee);
    }

    @Test
    void shouldDelegateUpdateTraineeProfileToTraineeService() {
        // Given
        Trainee trainee = buildTrainee();
        when(traineeService.updateProfile(trainee)).thenReturn(trainee);

        // When
        Trainee result = facade.updateTraineeProfile(trainee);

        // Then
        assertEquals(trainee, result);
        verify(traineeService).updateProfile(trainee);
    }

    @Test
    void shouldDelegateDeleteTraineeProfileToTraineeService() {
        // Given
        UUID traineeId = UUID.randomUUID();

        // When
        facade.deleteTraineeProfile(traineeId);

        // Then
        verify(traineeService).deleteProfile(traineeId);
    }

    @Test
    void shouldDelegateGetTraineeProfileByIdToTraineeService() {
        // Given
        UUID traineeId = UUID.randomUUID();
        Trainee trainee = buildTrainee();
        when(traineeService.getProfileById(traineeId)).thenReturn(trainee);

        // When
        Trainee result = facade.getTraineeProfileById(traineeId);

        // Then
        assertEquals(trainee, result);
        verify(traineeService).getProfileById(traineeId);
    }

    @Test
    void shouldDelegateCreateTrainerProfileToTrainerService() {
        // Given
        Trainer trainer = buildTrainer();
        when(trainerService.createProfile(trainer)).thenReturn(trainer);

        // When
        Trainer result = facade.createTrainerProfile(trainer);

        // Then
        assertEquals(trainer, result);
        verify(trainerService).createProfile(trainer);
    }

    @Test
    void shouldDelegateUpdateTrainerProfileToTrainerService() {
        // Given
        Trainer trainer = buildTrainer();
        when(trainerService.updateProfile(trainer)).thenReturn(trainer);

        // When
        Trainer result = facade.updateTrainerProfile(trainer);

        // Then
        assertEquals(trainer, result);
        verify(trainerService).updateProfile(trainer);
    }

    @Test
    void shouldDelegateGetTrainerProfileByIdToTrainerService() {
        // Given
        UUID trainerId = UUID.randomUUID();
        Trainer trainer = buildTrainer();
        when(trainerService.getProfileById(trainerId)).thenReturn(trainer);

        // When
        Trainer result = facade.getTrainerProfileById(trainerId);

        // Then
        assertEquals(trainer, result);
        verify(trainerService).getProfileById(trainerId);
    }

    @Test
    void shouldDelegateFindTrainersBySpecializationToTrainerService() {
        // Given
        List<Trainer> trainers = List.of(buildTrainer());
        when(trainerService.findBySpecialization(TrainingType.WEIGHT)).thenReturn(trainers);

        // When
        List<Trainer> result = facade.findTrainersBySpecialization(TrainingType.WEIGHT);

        // Then
        assertEquals(trainers, result);
        verify(trainerService).findBySpecialization(TrainingType.WEIGHT);
    }

    @Test
    void shouldDelegateCreateTrainingToTrainingService() {
        // Given
        Training training = buildTraining();
        when(trainingService.createTraining(training)).thenReturn(training);

        // When
        Training result = facade.createTraining(training);

        // Then
        assertEquals(training, result);
        verify(trainingService).createTraining(training);
    }

    @Test
    void shouldDelegateGetTrainingByIdToTrainingService() {
        // Given
        Training training = buildTraining();
        when(trainingService.getTrainingById(1L)).thenReturn(training);

        // When
        Training result = facade.getTrainingById(1L);

        // Then
        assertEquals(training, result);
        verify(trainingService).getTrainingById(1L);
    }

    @Test
    void shouldDelegateGetAllTrainingsToTrainingService() {
        // Given
        List<Training> trainings = List.of(buildTraining());
        when(trainingService.getAllTrainings()).thenReturn(trainings);

        // When
        List<Training> result = facade.getAllTrainings();

        // Then
        assertEquals(trainings, result);
        verify(trainingService).getAllTrainings();
    }

    @Test
    void shouldDelegateGetTrainingsByDateBetweenToTrainingService() {
        // Given
        Date from = new Date();
        Date to = new Date();
        List<Training> trainings = List.of(buildTraining());
        when(trainingService.getTrainingsByDateBetween(from, to)).thenReturn(trainings);

        // When
        List<Training> result = facade.getTrainingsByDateBetween(from, to);

        // Then
        assertEquals(trainings, result);
        verify(trainingService).getTrainingsByDateBetween(from, to);
    }

    private Trainee buildTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(UUID.randomUUID());
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        return trainee;
    }

    private Trainer buildTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(UUID.randomUUID());
        trainer.setFirstName("Robert");
        trainer.setLastName("Brown");
        trainer.setSpecialization(TrainingType.WEIGHT);
        return trainer;
    }

    private Training buildTraining() {
        Training training = new Training();
        training.setId(1L);
        training.setTraineeId(UUID.randomUUID());
        training.setTrainerId(UUID.randomUUID());
        training.setType(TrainingType.WEIGHT);
        training.setName("Morning Session");
        training.setDate(new Date());
        training.setDurationInMinutes(60);
        return training;
    }
}
