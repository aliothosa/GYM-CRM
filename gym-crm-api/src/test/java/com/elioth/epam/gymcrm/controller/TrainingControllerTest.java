package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.request.CreateTrainingRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTraineeTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTrainerTrainingsRestRequest;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/** Retains training endpoint success and exception-to-status mappings with JWT authentication. */
@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {
    @Mock private UserLogger userLogger; @Mock private TrainingService trainingService; @Mock private GymCrmMetrics metrics; @Mock private Authentication authentication;
    private TrainingController controller;
    @BeforeEach void setUp() { controller = new TrainingController(userLogger, trainingService, metrics); lenient().when(authentication.getName()).thenReturn("actor"); }

    @Test void mapsTrainingCreationFailures() {
        CreateTrainingRestRequest valid = mock(CreateTrainingRestRequest.class); CreateTrainingRestRequest missing = mock(CreateTrainingRestRequest.class); CreateTrainingRestRequest invalid = mock(CreateTrainingRestRequest.class);
        when(missing.traineeUsername()).thenReturn("missing"); when(missing.trainerUsername()).thenReturn("trainer"); when(invalid.traineeUsername()).thenReturn("trainee"); when(invalid.trainerUsername()).thenReturn("trainer");
        doNothing().when(trainingService).createTraining(valid); doThrow(new EntityNotFoundException("user")).when(trainingService).createTraining(missing); doThrow(new InvalidRequestException("invalid")).when(trainingService).createTraining(invalid);
        assertEquals(200, controller.addTraining(valid, authentication).getStatusCode().value()); assertEquals(400, controller.addTraining(missing, authentication).getStatusCode().value()); assertEquals(404, controller.addTraining(invalid, authentication).getStatusCode().value());
    }

    @Test void mapsTrainingSearchFailuresAndListsTypes() {
        GetTraineeTrainingsRestRequest trainee = mock(GetTraineeTrainingsRestRequest.class); GetTrainerTrainingsRestRequest trainer = mock(GetTrainerTrainingsRestRequest.class);
        when(trainingService.getTrainingsByTraineeUsernameAndCriteria("bad", trainee)).thenThrow(new InvalidRequestException("invalid")); when(trainingService.getTrainingsByTrainerUsernameAndCriteria("bad", trainer)).thenThrow(new InvalidRequestException("invalid"));
        assertEquals(200, controller.getAllTrainingTypes(authentication).getStatusCode().value()); assertEquals(400, controller.getTrainingsOfTrainee("bad", trainee, authentication).getStatusCode().value()); assertEquals(400, controller.getTrainingsOfTrainer("bad", trainer, authentication).getStatusCode().value());
    }
}
