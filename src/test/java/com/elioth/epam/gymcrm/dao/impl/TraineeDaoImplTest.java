package com.elioth.epam.gymcrm.dao.impl;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.storage.TraineeStorage;
import com.elioth.epam.gymcrm.storage.TrainerStorage;
import com.elioth.epam.gymcrm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeDaoImplTest {

    private TraineeDaoImpl traineeDao;
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
        trainerStorage = new TrainerStorage();
        trainingStorage = new TrainingStorage();

        traineeDao = new TraineeDaoImpl();
        traineeDao.setStorage(traineeStorage);
    }

    @Test
    void shouldCreateTraineeAndSaveEntityCorrectly() {
        // Given
        Trainee trainee = buildTrainee(UUID.randomUUID(), "John", "Smith");

        // When
        Trainee created = traineeDao.create(trainee);

        // Then
        assertEquals(trainee, created);
        assertTrue(traineeStorage.getData().containsKey(trainee.getId()));
        assertEquals("John", traineeStorage.getData().get(trainee.getId()).getFirstName());
    }

    @Test
    void shouldReturnOptionalWithTraineeWhenFindByIdExists() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee trainee = buildTrainee(id, "John", "Smith");
        traineeDao.create(trainee);

        // When
        Optional<Trainee> found = traineeDao.findById(id);

        // Then
        assertTrue(found.isPresent());
        assertEquals(trainee, found.get());
    }

    @Test
    void shouldReturnOptionalEmptyWhenFindByIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Optional<Trainee> found = traineeDao.findById(id);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllSavedTraineesWhenFindAll() {
        // Given
        Trainee first = buildTrainee(UUID.randomUUID(), "John", "Smith");
        Trainee second = buildTrainee(UUID.randomUUID(), "Maria", "Garcia");
        traineeDao.create(first);
        traineeDao.create(second);

        // When
        List<Trainee> all = traineeDao.findAll();

        // Then
        assertEquals(2, all.size());
        assertTrue(all.contains(first));
        assertTrue(all.contains(second));
    }

    @Test
    void shouldModifyExistingTraineeWhenUpdate() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee trainee = buildTrainee(id, "John", "Smith");
        traineeDao.create(trainee);

        Trainee updated = buildTrainee(id, "Jonathan", "Smith");
        updated.setUsername("Jonathan.Smith");

        // When
        Trainee result = traineeDao.update(updated);

        // Then
        assertEquals(updated, result);
        assertEquals("Jonathan", traineeStorage.getData().get(id).getFirstName());
    }

    @Test
    void shouldReturnNullWhenUpdateNonExistingTrainee() {
        // Given
        Trainee trainee = buildTrainee(UUID.randomUUID(), "John", "Smith");

        // When
        Trainee result = traineeDao.update(trainee);

        // Then
        assertNull(result);
        assertFalse(traineeStorage.getData().containsKey(trainee.getId()));
    }

    @Test
    void shouldRemoveTraineeWhenDelete() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee trainee = buildTrainee(id, "John", "Smith");
        traineeDao.create(trainee);

        // When
        traineeDao.delete(id);

        // Then
        assertFalse(traineeStorage.getData().containsKey(id));
        assertTrue(traineeDao.findById(id).isEmpty());
    }

    @Test
    void shouldNotMixTraineeStorageWithOtherStorages() {
        // Given
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        Trainee trainee = buildTrainee(traineeId, "John", "Smith");
        Trainer trainer = buildTrainer(trainerId, "Robert", "Brown");
        Training training = buildTraining(1L, traineeId, trainerId);

        // When
        traineeDao.create(trainee);
        trainerStorage.getData().put(trainerId, trainer);
        trainingStorage.getData().put(1L, training);

        // Then
        assertEquals(1, traineeStorage.getData().size());
        assertEquals(1, trainerStorage.getData().size());
        assertEquals(1, trainingStorage.getData().size());
        assertTrue(traineeStorage.getData().containsKey(traineeId));
        assertFalse(traineeStorage.getData().containsKey(trainerId));
        assertFalse(traineeStorage.getData().containsKey(1L));
    }

    private Trainee buildTrainee(UUID id, String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(firstName + "." + lastName);
        trainee.setPassword("password10");
        trainee.setActive(true);
        return trainee;
    }

    private Trainer buildTrainer(UUID id, String firstName, String lastName) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        return trainer;
    }

    private Training buildTraining(long id, UUID traineeId, UUID trainerId) {
        Training training = new Training();
        training.setId(id);
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        return training;
    }
}
