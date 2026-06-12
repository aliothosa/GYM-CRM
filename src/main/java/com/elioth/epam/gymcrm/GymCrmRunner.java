package com.elioth.epam.gymcrm;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.facade.GymCrmFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class GymCrmRunner implements CommandLineRunner {

    private final GymCrmFacade facade;

    @Autowired
    public GymCrmRunner(GymCrmFacade facade) {
        this.facade = facade;
    }

    @Override
    public void run(String... args) {
        System.out.println("========== GYM CRM FACADE RUNNER ==========");

        showInitialData();

        Trainee trainee = createTrainee();
        Trainer trainer = createTrainer();

        updateTrainee(trainee);
        updateTrainer(trainer);

        Training training = createTraining(trainee.getId(), trainer.getId());

        findData(trainee, trainer, training);

        deleteData(training.getId(), trainee.getId(), trainer.getId());

        System.out.println("========== RUNNER FINISHED ==========");
    }

    private void showInitialData() {
        System.out.println("\n--- Initial trainees ---");
        facade.getAllTraineeProfiles()
                .forEach(System.out::println);

        System.out.println("\n--- Initial trainers ---");
        facade.getAllTrainerProfiles()
                .forEach(System.out::println);

        System.out.println("\n--- Initial trainings ---");
        facade.getAllTrainings()
                .forEach(System.out::println);
    }

    private Trainee createTrainee() {
        System.out.println("\n--- Creating trainee ---");

        Trainee trainee = new Trainee();
        trainee.setFirstName("Carlos");
        trainee.setLastName("Ramirez");

        Trainee createdTrainee = facade.createTraineeProfile(trainee);

        System.out.println("Created trainee:");
        System.out.println(createdTrainee);

        return createdTrainee;
    }

    private Trainer createTrainer() {
        System.out.println("\n--- Creating trainer ---");

        Trainer trainer = new Trainer();
        trainer.setFirstName("Laura");
        trainer.setLastName("Mendoza");
        trainer.setSpecialization(TrainingType.WEIGHT);

        Trainer createdTrainer = facade.createTrainerProfile(trainer);

        System.out.println("Created trainer:");
        System.out.println(createdTrainer);

        return createdTrainer;
    }

    private void updateTrainee(Trainee trainee) {
        System.out.println("\n--- Updating trainee ---");

        trainee.setFirstName("Carlos Alberto");
        trainee.setLastName("Ramirez");

        Trainee updatedTrainee = facade.updateTraineeProfile(trainee);

        System.out.println("Updated trainee:");
        System.out.println(updatedTrainee);
    }

    private void updateTrainer(Trainer trainer) {
        System.out.println("\n--- Updating trainer ---");

        trainer.setFirstName("Laura");
        trainer.setLastName("Mendoza Lopez");
        trainer.setSpecialization(TrainingType.MOBILITY);

        Trainer updatedTrainer = facade.updateTrainerProfile(trainer);

        System.out.println("Updated trainer:");
        System.out.println(updatedTrainer);
    }

    private Training createTraining(UUID traineeId, UUID trainerId) {
        System.out.println("\n--- Creating training ---");

        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setType(TrainingType.MOBILITY);
        training.setName("Mobility Personal Session");
        training.setDate(new Date());
        training.setDurationInMinutes(50);

        Training createdTraining = facade.createTraining(training);

        System.out.println("Created training:");
        System.out.println(createdTraining);

        return createdTraining;
    }

    private void findData(Trainee trainee, Trainer trainer, Training training) {
        System.out.println("\n--- Finding trainee by id ---");
        System.out.println(facade.getTraineeProfileById(trainee.getId()));

        System.out.println("\n--- Finding trainee by username ---");
        System.out.println(facade.getTraineeProfileByUsername(trainee.getUsername()));

        System.out.println("\n--- Finding trainer by id ---");
        System.out.println(facade.getTrainerProfileById(trainer.getId()));

        System.out.println("\n--- Finding trainer by username ---");
        System.out.println(facade.getTrainerProfileByUsername(trainer.getUsername()));

        System.out.println("\n--- Finding trainers by specialization ---");
        List<Trainer> mobilityTrainers = facade.findTrainersBySpecialization(TrainingType.MOBILITY);
        mobilityTrainers.forEach(System.out::println);

        System.out.println("\n--- Finding training by id ---");
        System.out.println(facade.getTrainingById(training.getId()));

        System.out.println("\n--- Finding trainings by trainee id ---");
        facade.getTrainingsByTraineeId(trainee.getId())
                .forEach(System.out::println);

        System.out.println("\n--- Finding trainings by trainer id ---");
        facade.getTrainingsByTrainerId(trainer.getId())
                .forEach(System.out::println);

        System.out.println("\n--- Finding trainings by type ---");
        facade.getTrainingsByType(TrainingType.MOBILITY)
                .forEach(System.out::println);
    }

    private void deleteData(long trainingId, UUID traineeId, UUID trainerId) {
        System.out.println("\n--- Deleting created data ---");

        facade.deleteTraining(trainingId);
        System.out.println("Deleted training with id: " + trainingId);

        facade.deleteTraineeProfile(traineeId);
        System.out.println("Deleted trainee with id: " + traineeId);

        facade.deleteTrainerProfile(trainerId);
        System.out.println("Deleted trainer with id: " + trainerId);
    }
}