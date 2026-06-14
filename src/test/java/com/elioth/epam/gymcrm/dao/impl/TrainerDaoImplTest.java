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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerDaoImplTest {

    private TrainerDaoImpl trainerDao;
    private TrainerStorage trainerStorage;
    private TraineeStorage traineeStorage;
    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        trainerStorage = new TrainerStorage();
        traineeStorage = new TraineeStorage();
        trainingStorage = new TrainingStorage();

        trainerDao = new TrainerDaoImpl();
        trainerDao.setStorage(trainerStorage);
    }

    @Test
    void shouldCreateTrainerAndSaveEntityCorrectly() {
        // Given
        Trainer trainer = buildTrainer(UUID.randomUUID(), "Robert", "Brown");

        // When
        Trainer created = trainerDao.create(trainer);

        // Then
        assertEquals(trainer, created);
        assertTrue(trainerStorage.getData().containsKey(trainer.getId()));
        assertEquals("Robert", trainerStorage.getData().get(trainer.getId()).getFirstName());
    }

    @Test
    void shouldReturnOptionalWithTrainerWhenFindByIdExists() {
        // Given
        UUID id = UUID.randomUUID();
        Trainer trainer = buildTrainer(id, "Robert", "Brown");
        trainerDao.create(trainer);

        // When
        Optional<Trainer> found = trainerDao.findById(id);

        // Then
        assertTrue(found.isPresent());
        assertEquals(trainer, found.get());
    }

    @Test
    void shouldReturnOptionalEmptyWhenFindByIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Optional<Trainer> found = trainerDao.findById(id);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllSavedTrainersWhenFindAll() {
        // Given
        Trainer first = buildTrainer(UUID.randomUUID(), "Robert", "Brown");
        Trainer second = buildTrainer(UUID.randomUUID(), "Anna", "Taylor");
        trainerDao.create(first);
        trainerDao.create(second);

        // When
        List<Trainer> all = trainerDao.findAll();

        // Then
        assertEquals(2, all.size());
        assertTrue(all.contains(first));
        assertTrue(all.contains(second));
    }

    @Test
    void shouldModifyExistingTrainerWhenUpdate() {
        // Given
        UUID id = UUID.randomUUID();
        Trainer trainer = buildTrainer(id, "Robert", "Brown");
        trainerDao.create(trainer);

        Trainer updated = buildTrainer(id, "Bob", "Brown");
        updated.setSpecialization(TrainingType.MOBILITY);

        // When
        Trainer result = trainerDao.update(updated);

        // Then
        assertEquals(updated, result);
        assertEquals("Bob", trainerStorage.getData().get(id).getFirstName());
        assertEquals(TrainingType.MOBILITY, trainerStorage.getData().get(id).getSpecialization());
    }

    @Test
    void shouldReturnNullWhenUpdateNonExistingTrainer() {
        // Given
        Trainer trainer = buildTrainer(UUID.randomUUID(), "Robert", "Brown");

        // When
        Trainer result = trainerDao.update(trainer);

        // Then
        assertNull(result);
        assertFalse(trainerStorage.getData().containsKey(trainer.getId()));
    }

    @Test
    void shouldRemoveTrainerWhenDelete() {
        // Given
        UUID id = UUID.randomUUID();
        Trainer trainer = buildTrainer(id, "Robert", "Brown");
        trainerDao.create(trainer);

        // When
        trainerDao.delete(id);

        // Then
        assertFalse(trainerStorage.getData().containsKey(id));
        assertTrue(trainerDao.findById(id).isEmpty());
    }

    @Test
    void shouldNotMixTrainerStorageWithOtherStorages() {
        // Given
        UUID trainerId = UUID.randomUUID();
        UUID traineeId = UUID.randomUUID();

        Trainer trainer = buildTrainer(trainerId, "Robert", "Brown");
        Trainee trainee = buildTrainee(traineeId, "John", "Smith");
        Training training = buildTraining(1L, traineeId, trainerId);

        // When
        trainerDao.create(trainer);
        traineeStorage.getData().put(traineeId, trainee);
        trainingStorage.getData().put(1L, training);

        // Then
        assertEquals(1, trainerStorage.getData().size());
        assertEquals(1, traineeStorage.getData().size());
        assertEquals(1, trainingStorage.getData().size());
        assertTrue(trainerStorage.getData().containsKey(trainerId));
        assertFalse(trainerStorage.getData().containsKey(traineeId));
        assertFalse(trainerStorage.getData().containsKey(1L));
    }

    private Trainer buildTrainer(UUID id, String firstName, String lastName) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setUsername(firstName + "." + lastName);
        trainer.setPassword("password10");
        trainer.setActive(true);
        trainer.setSpecialization(TrainingType.WEIGHT);
        return trainer;
    }

    private Trainee buildTrainee(UUID id, String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        return trainee;
    }

    private Training buildTraining(long id, UUID traineeId, UUID trainerId) {
        Training training = new Training();
        training.setId(id);
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        return training;
    }
}
