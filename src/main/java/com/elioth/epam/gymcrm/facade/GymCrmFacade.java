package com.elioth.epam.gymcrm.facade;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class GymCrmFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymCrmFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // =========================
    // Trainee operations
    // =========================

    public Trainee createTraineeProfile(Trainee trainee) {
        return traineeService.createProfile(trainee);
    }

    public Trainee updateTraineeProfile(Trainee trainee) {
        return traineeService.updateProfile(trainee);
    }

    public void deleteTraineeProfile(UUID traineeId) {
        traineeService.deleteProfile(traineeId);
    }

    public Trainee getTraineeProfileById(UUID traineeId) {
        return traineeService.getProfileById(traineeId);
    }

    public List<Trainee> getAllTraineeProfiles() {
        return traineeService.getAllProfiles();
    }

    public Trainee getTraineeProfileByUsername(String username) {
        return traineeService.getProfileByUsername(username);
    }

    // =========================
    // Trainer operations
    // =========================

    public Trainer createTrainerProfile(Trainer trainer) {
        return trainerService.createProfile(trainer);
    }

    public Trainer updateTrainerProfile(Trainer trainer) {
        return trainerService.updateProfile(trainer);
    }

    public void deleteTrainerProfile(UUID trainerId) {
        trainerService.deleteProfile(trainerId);
    }

    public Trainer getTrainerProfileById(UUID trainerId) {
        return trainerService.getProfileById(trainerId);
    }

    public List<Trainer> getAllTrainerProfiles() {
        return trainerService.getAllProfiles();
    }

    public Trainer getTrainerProfileByUsername(String username) {
        return trainerService.getProfileByUsername(username);
    }

    public List<Trainer> findTrainersBySpecialization(TrainingType specialization) {
        return trainerService.findBySpecialization(specialization);
    }

    // =========================
    // Training operations
    // =========================

    public Training createTraining(Training training) {
        return trainingService.createTraining(training);
    }

    public Training updateTraining(Training training) {
        return trainingService.updateTraining(training);
    }

    public void deleteTraining(long trainingId) {
        trainingService.deleteTraining(trainingId);
    }

    public Training getTrainingById(long trainingId) {
        return trainingService.getTrainingById(trainingId);
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public List<Training> getTrainingsByTraineeId(UUID traineeId) {
        return trainingService.getTrainingsByTraineeId(traineeId);
    }

    public List<Training> getTrainingsByTrainerId(UUID trainerId) {
        return trainingService.getTrainingsByTrainerId(trainerId);
    }

    public List<Training> getTrainingsByType(TrainingType type) {
        return trainingService.getTrainingsByType(type);
    }

    public List<Training> getTrainingsByDateBetween(Date from, Date to) {
        return trainingService.getTrainingsByDateBetween(from, to);
    }
}