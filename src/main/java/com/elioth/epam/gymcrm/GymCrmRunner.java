package com.elioth.epam.gymcrm;

import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GymCrmRunner implements CommandLineRunner {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymCrmRunner(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Trainees ===");
        traineeService.getAllProfiles().forEach(System.out::println);

        System.out.println("=== Trainers ===");
        trainerService.getAllProfiles().forEach(System.out::println);

        System.out.println("=== Trainings ===");
        trainingService.getAllTrainings().forEach(System.out::println);
    }
}