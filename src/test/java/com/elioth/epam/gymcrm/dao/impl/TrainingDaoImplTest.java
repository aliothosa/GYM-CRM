package com.elioth.epam.gymcrm.dao.impl;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.storage.TraineeStorage;
import com.elioth.epam.gymcrm.storage.TrainerStorage;
import com.elioth.epam.gymcrm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingDaoImplTest {

    private TrainingDaoImpl trainingDao;
    private TrainingStorage trainingStorage;
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;

    @BeforeEach
    void setUp() {
        trainingStorage = new TrainingStorage();
        traineeStorage = new TraineeStorage();
        trainerStorage = new TrainerStorage();

        trainingDao = new TrainingDaoImpl();
        trainingDao.setStorage(trainingStorage);
    }

    @Test
    void shouldCreateTrainingAndSaveEntityCorrectly() {
        // Given
        Training training = buildTraining(1L, UUID.randomUUID(), UUID.randomUUID());

        // When
        Training created = trainingDao.create(training);

        // Then
        assertEquals(training, created);
        assertTrue(trainingStorage.getData().containsKey(training.getId()));
        assertEquals("Morning Session", trainingStorage.getData().get(1L).getName());
    }

    @Test
    void shouldReturnOptionalWithTrainingWhenFindByIdExists() {
        // Given
        Training training = buildTraining(1L, UUID.randomUUID(), UUID.randomUUID());
        trainingDao.create(training);

        // When
        Optional<Training> found = trainingDao.findById(1L);

        // Then
        assertTrue(found.isPresent());
        assertEquals(training, found.get());
    }

    @Test
    void shouldReturnOptionalEmptyWhenFindByIdDoesNotExist() {
        // When
        Optional<Training> found = trainingDao.findById(99L);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllSavedTrainingsWhenFindAll() {
        // Given
        Training first = buildTraining(1L, UUID.randomUUID(), UUID.randomUUID());
        Training second = buildTraining(2L, UUID.randomUUID(), UUID.randomUUID());
        trainingDao.create(first);
        trainingDao.create(second);

        // When
        List<Training> all = trainingDao.findAll();

        // Then
        assertEquals(2, all.size());
        assertTrue(all.contains(first));
        assertTrue(all.contains(second));
    }

    @Test
    void shouldModifyExistingTrainingWhenUpdate() {
        // Given
        Training training = buildTraining(1L, UUID.randomUUID(), UUID.randomUUID());
        trainingDao.create(training);

        Training updated = buildTraining(1L, training.getTraineeId(), training.getTrainerId());
        updated.setName("Evening Session");
        updated.setDurationInMinutes(90);

        // When
        Training result = trainingDao.update(updated);

        // Then
        assertEquals(updated, result);
        assertEquals("Evening Session", trainingStorage.getData().get(1L).getName());
        assertEquals(90, trainingStorage.getData().get(1L).getDurationInMinutes());
    }

    @Test
    void shouldReturnNullWhenUpdateNonExistingTraining() {
        // Given
        Training training = buildTraining(1L, UUID.randomUUID(), UUID.randomUUID());

        // When
        Training result = trainingDao.update(training);

        // Then
        assertNull(result);
        assertFalse(trainingStorage.getData().containsKey(1L));
    }

    @Test
    void shouldRemoveTrainingWhenDelete() {
        // Given
        Training training = buildTraining(1L, UUID.randomUUID(), UUID.randomUUID());
        trainingDao.create(training);

        // When
        trainingDao.delete(1L);

        // Then
        assertFalse(trainingStorage.getData().containsKey(1L));
        assertTrue(trainingDao.findById(1L).isEmpty());
    }

    @Test
    void shouldNotMixTrainingStorageWithOtherStorages() {
        // Given
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        Training training = buildTraining(1L, traineeId, trainerId);
        Trainee trainee = buildTrainee(traineeId);
        Trainer trainer = buildTrainer(trainerId);

        // When
        trainingDao.create(training);
        traineeStorage.getData().put(traineeId, trainee);
        trainerStorage.getData().put(trainerId, trainer);

        // Then
        assertEquals(1, trainingStorage.getData().size());
        assertEquals(1, traineeStorage.getData().size());
        assertEquals(1, trainerStorage.getData().size());
        assertTrue(trainingStorage.getData().containsKey(1L));
        assertFalse(trainingStorage.getData().containsKey(traineeId));
        assertFalse(trainingStorage.getData().containsKey(trainerId));
    }

    private Training buildTraining(long id, UUID traineeId, UUID trainerId) {
        Training training = new Training();
        training.setId(id);
        training.setTraineeId(traineeId);
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
        return trainer;
    }
}
