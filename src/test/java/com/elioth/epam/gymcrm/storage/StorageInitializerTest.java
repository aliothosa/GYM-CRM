package com.elioth.epam.gymcrm.storage;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageInitializerTest {

    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
        trainerStorage = new TrainerStorage();
        trainingStorage = new TrainingStorage();

        Resource testFile = new ClassPathResource("test-initial-storage.json");
        StorageInitializer initializer = new StorageInitializer(
                traineeStorage,
                trainerStorage,
                trainingStorage,
                new ObjectMapper(),
                testFile
        );
        initializer.init();
    }

    @Test
    void shouldLoadPreparedDataFromTestFileIntoStorages() {
        // Given
        UUID traineeId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID trainerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        // When
        Trainee trainee = traineeStorage.getData().get(traineeId);
        Trainer trainer = trainerStorage.getData().get(trainerId);
        Training training = trainingStorage.getData().get(100L);

        // Then
        assertEquals(1, traineeStorage.getData().size());
        assertEquals(1, trainerStorage.getData().size());
        assertEquals(1, trainingStorage.getData().size());

        assertNotNull(trainee);
        assertEquals("Test", trainee.getFirstName());
        assertEquals("Trainee", trainee.getLastName());
        assertEquals("Test.Trainee", trainee.getUsername());

        assertNotNull(trainer);
        assertEquals("Test", trainer.getFirstName());
        assertEquals("Trainer", trainer.getLastName());
        assertEquals(TrainingType.AEROBIC, trainer.getSpecialization());

        assertNotNull(training);
        assertEquals("Test Training Session", training.getName());
        assertEquals(traineeId, training.getTraineeId());
        assertEquals(trainerId, training.getTrainerId());
        assertTrue(training.getDurationInMinutes() > 0);
    }
}
