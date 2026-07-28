package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTraineeTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTrainerTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.mockito.Mockito.when;

/** Exercises each migrated controller entry point with Spring Security Authentication. */
@ExtendWith(MockitoExtension.class)
class SecurityMigratedControllerTest {
    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;
    @Mock private UserLogger userLogger;
    @Mock private GymCrmMetrics metrics;
    @Mock private Authentication authentication;
    @Mock private CreateTraineeRequest createTraineeRequest;
    @Mock private CreateTrainerRequest createTrainerRequest;
    @Mock private UpdateTraineeRequest updateTraineeRequest;
    @Mock private UpdateTrainerRequest updateTrainerRequest;
    @Mock private CreateTrainingRestRequest createTrainingRequest;
    @Mock private GetTraineeTrainingsRestRequest traineeTrainingsRequest;
    @Mock private GetTrainerTrainingsRestRequest trainerTrainingsRequest;

    private TraineeController traineeController;
    private TrainerController trainerController;
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("Emily.Davis");
        traineeController = new TraineeController(traineeService, userLogger, metrics);
        trainerController = new TrainerController(trainerService, userLogger, metrics);
        trainingController = new TrainingController(userLogger, trainingService, metrics);
    }

    @Test
    void traineeEndpointsUseAuthenticationInsteadOfSession() {
        traineeController.addTrainee(createTraineeRequest);
        traineeController.getTraineeProfile("Emily.Davis");
        traineeController.updateTraineeProfile("Emily.Davis", updateTraineeRequest, authentication);
        traineeController.deleteTraineeProfile("Emily.Davis", authentication);
        traineeController.updateTraineeList("Emily.Davis", Set.of("John.Doe"), authentication);
        traineeController.setStatus("Emily.Davis", true, authentication);
    }

    @Test
    void trainerEndpointsUseAuthenticationInsteadOfSession() {
        trainerController.createTrainer(createTrainerRequest);
        trainerController.getTrainer("John.Doe", authentication);
        trainerController.updateTrainer("John.Doe", updateTrainerRequest, authentication);
        trainerController.getAllTrainersNotAssignedToTrainee("Emily.Davis", authentication);
        trainerController.setStatus("John.Doe", true, authentication);
    }

    @Test
    void trainingEndpointsUseAuthenticationInsteadOfSession() {
        trainingController.addTraining(createTrainingRequest, authentication);
        trainingController.getAllTrainingTypes(authentication);
        trainingController.getTrainingsOfTrainee("Emily.Davis", traineeTrainingsRequest, authentication);
        trainingController.getTrainingsOfTrainer("John.Doe", trainerTrainingsRequest, authentication);
    }
}
