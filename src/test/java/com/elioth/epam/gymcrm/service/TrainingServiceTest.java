package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.dao.TraineeDao;
import com.elioth.epam.gymcrm.dao.TrainerDao;
import com.elioth.epam.gymcrm.dao.TrainingDao;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainingService trainingService;

    private UUID traineeId;
    private UUID trainerProfileId;

    @BeforeEach
    void setUp() {
        trainingService.setTrainingDao(trainingDao);
        trainingService.setTraineeDao(traineeDao);
        trainingService.setTrainerDao(trainerDao);

        traineeId = UUID.randomUUID();
        trainerProfileId = UUID.randomUUID();
    }

    @Test
    void shouldCreateTrainingCorrectly() {
        // Given
        Training training = buildTraining(0L, traineeId, trainerProfileId);
        stubRelatedProfilesExist();
        when(trainingDao.findAll()).thenReturn(List.of());
        when(trainingDao.create(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Training created = trainingService.createTraining(training);

        // Then
        assertEquals(1L, created.getId());
        assertEquals("Morning Session", created.getName());
        verify(trainingDao).create(training);
    }

    @Test
    void shouldReturnExistingTrainingWhenGetTrainingById() {
        // Given
        Training training = buildTraining(1L, traineeId, trainerProfileId);
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));

        // When
        Training found = trainingService.getTrainingById(1L);

        // Then
        assertEquals(training, found);
    }

    @Test
    void shouldReturnAllTrainingsWhenGetAllTrainings() {
        // Given
        Training first = buildTraining(1L, traineeId, trainerProfileId);
        Training second = buildTraining(2L, traineeId, trainerProfileId);
        when(trainingDao.findAll()).thenReturn(List.of(first, second));

        // When
        List<Training> all = trainingService.getAllTrainings();

        // Then
        assertEquals(2, all.size());
        assertEquals(first, all.get(0));
        assertEquals(second, all.get(1));
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenTrainingIsNullOnCreate() {
        // When / Then
        assertThrows(InvalidEntityException.class, () -> trainingService.createTraining(null));
        verify(trainingDao, never()).create(any());
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenTrainingNameIsBlankOnCreate() {
        // Given
        Training training = buildTraining(0L, traineeId, trainerProfileId);
        training.setName(" ");

        // When / Then
        assertThrows(InvalidEntityException.class, () -> trainingService.createTraining(training));
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenDurationIsInvalidOnCreate() {
        // Given
        Training training = buildTraining(0L, traineeId, trainerProfileId);
        training.setDurationInMinutes(0);

        // When / Then
        assertThrows(InvalidEntityException.class, () -> trainingService.createTraining(training));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTraineeDoesNotExistOnCreate() {
        // Given
        Training training = buildTraining(0L, traineeId, trainerProfileId);
        when(traineeDao.findById(traineeId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> trainingService.createTraining(training));
        verify(trainingDao, never()).create(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGetTrainingByIdDoesNotExist() {
        // Given
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> trainingService.getTrainingById(99L));
    }

    private void stubRelatedProfilesExist() {
        when(traineeDao.findById(traineeId)).thenReturn(Optional.of(buildTrainee(traineeId)));
        when(trainerDao.findById(trainerProfileId)).thenReturn(Optional.of(buildTrainer(trainerProfileId)));
    }

    private Training buildTraining(long id, UUID traineeProfileId, UUID trainerId) {
        Training training = new Training();
        training.setId(id);
        training.setTraineeId(traineeProfileId);
        training.setTrainerId(trainerId);
        training.setType(TrainingType.WEIGHT);
        training.setName("Morning Session");
        training.setDate(new Date());
        training.setDurationInMinutes(60);
        return training;
    }

    private Trainee buildTrainee(UUID id) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        return trainee;
    }

    private Trainer buildTrainer(UUID id) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setFirstName("Robert");
        trainer.setLastName("Brown");
        trainer.setSpecialization(TrainingType.WEIGHT);
        return trainer;
    }
}
