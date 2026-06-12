package com.elioth.epam.gymcrm.storage;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class StorageInitializer {

    private final TraineeStorage traineeStorage;
    private final TrainerStorage trainerStorage;
    private final TrainingStorage trainingStorage;
    private final ObjectMapper objectMapper;
    private final Resource initialStorageFile;

    public StorageInitializer(
            TraineeStorage traineeStorage,
            TrainerStorage trainerStorage,
            TrainingStorage trainingStorage,
            ObjectMapper objectMapper,
            @Value("${storage.init.file}") Resource initialStorageFile
    ) {
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
        this.objectMapper = objectMapper;
        this.initialStorageFile = initialStorageFile;
    }

    @PostConstruct
    public void init() {
        try (InputStream inputStream = initialStorageFile.getInputStream()) {
            InitialStorageData initialData =
                    objectMapper.readValue(inputStream, InitialStorageData.class);

            if (initialData.getTrainees() != null) {
                for (Trainee trainee : initialData.getTrainees()) {
                    traineeStorage.getData().put(trainee.getId(), trainee);
                }
            }

            if (initialData.getTrainers() != null) {
                for (Trainer trainer : initialData.getTrainers()) {
                    trainerStorage.getData().put(trainer.getId(), trainer);
                }
            }

            if (initialData.getTrainings() != null) {
                for (Training training : initialData.getTrainings()) {
                    trainingStorage.getData().put(training.getId(), training);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("Error loading initial storage data", e);
        }
    }
    @Setter
    @Getter
    public static class InitialStorageData {
        private List<Trainee> trainees;
        private List<Trainer> trainers;
        private List<Training> trainings;

    }
}