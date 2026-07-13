package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTraineeTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTrainerTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private UserLogger userLogger;
    @Mock
    private TrainingService trainingService;

    private TrainingController controller;
    private AuthSession session;

    @BeforeEach
    void setUp() {
        controller = new TrainingController(userLogger, trainingService);
        session = new AuthSession(1L, "actor", Role.TRAINEE);
    }

    @Test
    void shouldAddTrainingAndMapFailures() {
        CreateTrainingRestRequest valid = request("trainee", "trainer");
        CreateTrainingRestRequest missing = request("missing", "trainer");
        CreateTrainingRestRequest invalid = request("trainee", "invalid");
        doNothing().when(trainingService).createTraining(valid);
        doThrow(new EntityNotFoundException("user"))
                .when(trainingService).createTraining(missing);
        doThrow(new InvalidRequestException("invalid"))
                .when(trainingService).createTraining(invalid);

        assertEquals(HttpStatus.OK, controller.addTraining(valid, session).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.addTraining(missing, session).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                controller.addTraining(invalid, session).getStatusCode());
    }

    @Test
    void shouldGetTrainingTypes() {
        List<String> body = List.of("Fitness", "Yoga");
        when(trainingService.getTrainingTypeNames()).thenReturn(body);

        assertResponse(controller.getAllTrainingTypes(session), HttpStatus.OK, body);
    }

    @Test
    void shouldGetTraineeTrainingsOrReturnBadRequest() {
        GetTraineeTrainingsRestRequest valid = mock(GetTraineeTrainingsRestRequest.class);
        GetTraineeTrainingsRestRequest invalid = mock(GetTraineeTrainingsRestRequest.class);
        List<TraineeTrainingResponse> body = List.of(mock(TraineeTrainingResponse.class));
        when(trainingService.getTrainingsByTraineeUsernameAndCriteria("known", valid))
                .thenReturn(body);
        when(trainingService.getTrainingsByTraineeUsernameAndCriteria("known", invalid))
                .thenThrow(new InvalidRequestException("invalid"));

        assertResponse(controller.getTrainingsOfTrainee("known", valid, session),
                HttpStatus.OK, body);
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.getTrainingsOfTrainee("known", invalid, session).getStatusCode());
    }

    @Test
    void shouldGetTrainerTrainingsOrReturnBadRequest() {
        GetTrainerTrainingsRestRequest valid = mock(GetTrainerTrainingsRestRequest.class);
        GetTrainerTrainingsRestRequest invalid = mock(GetTrainerTrainingsRestRequest.class);
        List<TrainerTrainingResponse> body = List.of(mock(TrainerTrainingResponse.class));
        when(trainingService.getTrainingsByTrainerUsernameAndCriteria("known", valid))
                .thenReturn(body);
        when(trainingService.getTrainingsByTrainerUsernameAndCriteria("known", invalid))
                .thenThrow(new InvalidRequestException("invalid"));

        assertResponse(controller.getTrainingsOfTrainer("known", valid, session),
                HttpStatus.OK, body);
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.getTrainingsOfTrainer("known", invalid, session).getStatusCode());
    }

    private CreateTrainingRestRequest request(String trainee, String trainer) {
        CreateTrainingRestRequest request = mock(CreateTrainingRestRequest.class);
        when(request.traineeUsername()).thenReturn(trainee);
        when(request.trainerUsername()).thenReturn(trainer);
        return request;
    }

    private void assertResponse(ResponseEntity<?> response, HttpStatus status, Object body) {
        assertEquals(status, response.getStatusCode());
        assertSame(body, response.getBody());
    }
}
