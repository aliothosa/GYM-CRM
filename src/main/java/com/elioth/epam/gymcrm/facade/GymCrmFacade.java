package com.elioth.epam.gymcrm.facade;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class GymCrmFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymCrmFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // =========================
    // Trainee operations
    // =========================

    public Trainee createTraineeProfile(Trainee trainee) {
        throw new UnsupportedOperationException("TODO");
    }

    public Trainee updateTraineeProfile(Trainee trainee) {
        throw new UnsupportedOperationException("TODO");
    }

    public void deleteTraineeProfile(UUID traineeId) {
        throw new UnsupportedOperationException("TODO");
    }

    public Trainee getTraineeProfileById(UUID traineeId) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Trainee> getAllTraineeProfiles() {
        throw new UnsupportedOperationException("TODO");
    }

    public Trainee getTraineeProfileByUsername(String username) {
        throw new UnsupportedOperationException("TODO");
    }

    // =========================
    // Trainer operations
    // =========================

    public Trainer createTrainerProfile(Trainer trainer) {
        throw new UnsupportedOperationException("TODO");
    }

    public Trainer updateTrainerProfile(Trainer trainer) {
        throw new UnsupportedOperationException("TODO");
    }

    public void deleteTrainerProfile(UUID trainerId) {
        throw new UnsupportedOperationException("TODO");
    }

    public Trainer getTrainerProfileById(UUID trainerId) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Trainer> getAllTrainerProfiles() {
        throw new UnsupportedOperationException("TODO");
    }

    public Trainer getTrainerProfileByUsername(String username) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Trainer> findTrainersBySpecialization(TrainingType specialization) {
        throw new UnsupportedOperationException("TODO");
    }

    // =========================
    // Training operations
    // =========================

    public Training createTraining(Training training) {
        throw new UnsupportedOperationException("TODO");
    }

    public Training updateTraining(Training training) {
        throw new UnsupportedOperationException("TODO");
    }

    public void deleteTraining(long trainingId) {
        throw new UnsupportedOperationException("TODO");
    }

    public Training getTrainingById(long trainingId) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Training> getAllTrainings() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Training> getTrainingsByTraineeId(UUID traineeId) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Training> getTrainingsByTrainerId(UUID trainerId) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Training> getTrainingsByType(TrainingType type) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Training> getTrainingsByDateBetween(Date from, Date to) {
        throw new UnsupportedOperationException("TODO");
    }
}